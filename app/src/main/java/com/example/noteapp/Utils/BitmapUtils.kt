package com.example.noteapp.Utils

import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream

object BitmapUtils {

    fun getBitmapFromAssets(context: Context,fileName :String,width :Int,height :Int): Bitmap? {
        val assetManager = context.assets
        val inputStream: InputStream
        val bitmap:Bitmap? = null
        try {
            val option: BitmapFactory.Options = BitmapFactory.Options()
            option.inJustDecodeBounds =true
            inputStream = assetManager.open(fileName)
            option.inSampleSize = calculateInSampleSize(option,width,height)
            option.inJustDecodeBounds =false
            return BitmapFactory.decodeStream(inputStream,null,option)
        }catch (e: IOException){
            Log.e("DEBUG", e.message!!)
        }
        return null
    }

    fun getBitmapFromGallery(context: Context,path: Uri,width: Int,height: Int):Bitmap? {
        val filePatchColume = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = context.contentResolver.query(path,filePatchColume,null,null,null)
        cursor!!.moveToFirst()
        val columeIndex = cursor.getColumnIndex(filePatchColume[0])
        val picturePatch =cursor.getString(columeIndex)
        cursor.close()

        val options = BitmapFactory.Options()
        options.inJustDecodeBounds =true
        BitmapFactory.decodeFile(picturePatch,options)
        options.inSampleSize = calculateInSampleSize(options,width,height)
        options.inJustDecodeBounds =false
        return BitmapFactory.decodeFile(picturePatch,options)
    }

    fun insertImage(contentResolver: ContentResolver,source :Bitmap?,title :String,description :String):String?{
        val value = ContentValues()
        value.put(MediaStore.Images.Media.TITLE,title)
        value.put(MediaStore.Images.Media.DISPLAY_NAME,title)
        value.put(MediaStore.Images.Media.DESCRIPTION,description)
        value.put(MediaStore.Images.Media.MIME_TYPE,"image/jpeg")
        value.put(MediaStore.Images.Media.DATE_ADDED,System.currentTimeMillis())
        value.put(MediaStore.Images.Media.DATE_TAKEN,System.currentTimeMillis())

        var url: Uri? =null
        var stringUrl:String? = null
        try {
            url = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,value)
            if (source!=null){
                val imageOut = contentResolver.openOutputStream(url!!)
                try {
                    source.compress(Bitmap.CompressFormat.JPEG,50,imageOut)
                }finally {
                    imageOut!!.close()
                }
                val id = ContentUris.parseId(url)
                val miniThum = MediaStore.Images.Thumbnails.getThumbnail(contentResolver,id,MediaStore.Images.Thumbnails.MINI_KIND,null)
                storeThubnail(contentResolver, miniThum,id,50f,50f,MediaStore.Images.Thumbnails.MINI_KIND)
            }else{
                contentResolver.delete(url!!,null,null)
                url = null
            }
        }catch (e : Exception){
            if (url!=null){
                contentResolver.delete(url,null,null)
                url = null
            }
            e.printStackTrace()
        }
        if (url!=null){
            stringUrl = url.toString()
        }
        return stringUrl

    }

    private fun storeThubnail(contentResolver: ContentResolver, source: Bitmap?, id:Long, width: Float, height: Float, miniKind: Int): Bitmap? {
        val matrix = Matrix()
        val scaleX = width/source!!.width
        val scaleY = height/ source.height
        matrix.setScale(scaleX,scaleY)

        val thumb = Bitmap.createBitmap(source,0,0,source.width,source.height,matrix,true)

        val value = ContentValues(4)
        value.put(MediaStore.Images.Thumbnails.KIND,miniKind)
        value.put(MediaStore.Images.Thumbnails.IMAGE_ID,id.toInt())
        value.put(MediaStore.Images.Thumbnails.HEIGHT,thumb.height)
        value.put(MediaStore.Images.Thumbnails.WIDTH,thumb.width)

        val url = contentResolver.insert(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI,value)

        try {
            val thumbOut = contentResolver.openOutputStream(url!!)
            thumb.compress(Bitmap.CompressFormat.JPEG,100,thumbOut)
            thumbOut!!.close()
            return thumb
        }catch (ex:FileNotFoundException){
            return null
            ex.printStackTrace()
        }catch (ex:IOException){
            return null
            ex.printStackTrace()
        }
    }

    fun applyOverlay(
        context: Context,
        sourceImage: Bitmap,
        overlayDrawableResourceId: Int
    ): Bitmap? =
        try {
            val width = sourceImage.width
            val height = sourceImage.height
            val resources = context.resources

            val imageAsDrawable = BitmapDrawable(resources, sourceImage)
            val layers = arrayOfNulls<Drawable>(2)

            layers[0] = imageAsDrawable
            layers[1] = BitmapDrawable(
                resources,
                decodeSampledBitmapFromResource(
                    resources,
                    overlayDrawableResourceId,
                    width,
                    height
                )
            )
            val layerDrawable = LayerDrawable(layers)
            drawableToBitmap(layerDrawable)
        } catch (ex: Exception) {
            null
        }

    fun decodeSampledBitmapFromResource(
        res: Resources, resId: Int,
        reqWidth: Int, reqHeight: Int
    ): Bitmap {

        // First decode with inJustDecodeBounds=true to check dimensions
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeResource(res, resId, options)

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false
        return BitmapFactory.decodeResource(res, resId, options)
    }

    fun calculateInSampleSize(
        options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int
    ): Int {
        // Raw height and width of image
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {

            val halfHeight = height / 2
            val halfWidth = width / 2

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }

    fun drawableToBitmap(drawable: Drawable): Bitmap {
        if (drawable is BitmapDrawable) {
            if (drawable.bitmap != null) {
                return drawable.bitmap
            }
        }

        val bitmap = if (drawable.intrinsicWidth <= 0 || drawable.intrinsicHeight <= 0) {
            Bitmap.createBitmap(
                1,
                1,
                Bitmap.Config.ARGB_8888
            ) // Single color bitmap will be created of 1x1 pixel
        } else {
            Bitmap.createBitmap(
                drawable.intrinsicWidth,
                drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
        }

        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }
}