package com.example.noteapp

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.Typeface
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.noteapp.Interface.AddTextFragmentListener
import com.example.noteapp.Interface.EditImageFragmentListener
import com.example.noteapp.Interface.FilterListFragmentListener
import com.example.noteapp.Interface.IconFragmentListener
import com.example.noteapp.Utils.BitmapUtils
import com.example.noteapp.Utils.NonSwipeableViewPage
import com.example.noteapp.adapter.ViewPagerAdapter
import com.example.noteapp.fragment.AddTextFragment
import com.example.noteapp.fragment.EditImageFragment
import com.example.noteapp.fragment.FilterListFragment
import com.example.noteapp.fragment.IconFragment
import com.example.noteapp.model.Image
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.zomato.photofilters.imageprocessors.Filter
import com.zomato.photofilters.imageprocessors.subfilters.BrightnessSubFilter
import com.zomato.photofilters.imageprocessors.subfilters.ContrastSubFilter
import com.zomato.photofilters.imageprocessors.subfilters.SaturationSubfilter
import ja.burhanrashid52.photoeditor.OnSaveBitmap
import ja.burhanrashid52.photoeditor.PhotoEditor
import ja.burhanrashid52.photoeditor.PhotoEditorView
import kotlinx.android.synthetic.main.activity_suaanh.*
import kotlinx.android.synthetic.main.content_main.*
import java.io.ByteArrayOutputStream
import java.lang.Math.sqrt
import java.util.*
import kotlin.math.pow
import kotlin.math.roundToInt

class SuaAnhActivity : AppCompatActivity(), FilterListFragmentListener, EditImageFragmentListener,
    IconFragmentListener, AddTextFragmentListener {
    val SELECT_GALLERY_PERMISSION = 1000
    private val PICK_IMAGE_REQUEST = 111
    private var firebaseStorage: FirebaseStorage? = null
    private var storageReference: StorageReference? = null
    private var databaseReference: DatabaseReference? = null
    private var mAuth: FirebaseAuth? = null
    private var fbUser: FirebaseUser? = null
    lateinit var photoEditor: PhotoEditor

    init {
        System.loadLibrary("NativeImageProcessor")
    }

    private var originalImage: Bitmap? = null
    private lateinit var filteredImage: Bitmap
    internal lateinit var finalImage: Bitmap
    private lateinit var image_preview: PhotoEditorView

    private lateinit var filterListFragment: FilterListFragment
    private lateinit var editImageFragment: EditImageFragment
    private lateinit var iconFragment: IconFragment
    private lateinit var addTextFragment: AddTextFragment

    private var brightnessFinal = 0
    private var saturationFinal = 1.0f
    private var constrantFinal = 1.0f

    internal var imageUri: Uri? = null
    internal val CAMERA_REQUEST: Int = 9999

    private var mSensorManager: SensorManager? = null
    private var acceleration = 0f
    private var currentAcceleration = 0f
    private var lastAcceleration = 0f

    private val SHAKE_SLOP_TIME_MS = 999999
    private val SHAKE_COUNT_RESET_TIME_MS = 5000

    object Main {
        val IMAGE_NAME = "flash.jpg"

    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_suaanh)
        setSupportActionBar(toolbar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Filter"
        mAuth = FirebaseAuth.getInstance()
        fbUser = mAuth!!.currentUser
        firebaseStorage = FirebaseStorage.getInstance()
        storageReference = FirebaseStorage.getInstance().getReference(fbUser!!.uid)
        databaseReference = FirebaseDatabase.getInstance().getReference("Users")
//        databaseReference = FirebaseDatabase.getInstance().getReference("updates")
        image_preview = findViewById(R.id.image_preview)

        val a = intent.getBooleanExtra("a", false)
        if (a)
            openCamera()
        loadImage()
        setupViewPager(viewPager)
        tabs.setupWithViewPager(viewPager)
        photoEditor = PhotoEditor.Builder(this, image_preview)
            .setPinchTextScalable(true)
            .setDefaultEmojiTypeface(Typeface.createFromAsset(assets, "emojione-android.ttf"))
            .build()
        filterListFragment = FilterListFragment.getInstance(null)
        editImageFragment = EditImageFragment.getInstance()
        iconFragment = IconFragment.getInstance()
        addTextFragment = AddTextFragment.getInstance()

        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        Objects.requireNonNull(mSensorManager)!!.registerListener(
            sensorListener, mSensorManager!!
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL
        )
        acceleration = 10f
        currentAcceleration = SensorManager.GRAVITY_EARTH
        lastAcceleration = SensorManager.GRAVITY_EARTH

        btn_addicon.setOnClickListener {
            iconFragment.setListener(this)
            iconFragment.show(supportFragmentManager, iconFragment.tag)
        }


        btn_addtext.setOnClickListener {
            addTextFragment.setLintener(this)
            addTextFragment.show(supportFragmentManager, addTextFragment.tag)
        }
    }

    private fun setupViewPager(viewPager: NonSwipeableViewPage?) {
        val adapter = ViewPagerAdapter(supportFragmentManager)
        filterListFragment = FilterListFragment()
        filterListFragment.setListener(this)

        editImageFragment = EditImageFragment()
        editImageFragment.setListener(this)

        adapter.addFragment(filterListFragment, "FILTERS")
        adapter.addFragment(editImageFragment, "EDIT")

        viewPager?.adapter = adapter

    }

    private fun loadImage() {
        originalImage = BitmapUtils.getBitmapFromAssets(this, Main.IMAGE_NAME, 200, 300)
        filteredImage = originalImage!!.copy(Bitmap.Config.ARGB_8888, true)
        finalImage = originalImage!!.copy(Bitmap.Config.ARGB_8888, true)
        image_preview.source.setImageBitmap(originalImage)
    }

    override fun onFilterSelected(filter: Filter) {
        resetControl()
        filteredImage = originalImage!!.copy(Bitmap.Config.ARGB_8888, true)
        Log.e("halo", "ddaay")
        image_preview.source.setImageBitmap(filter.processFilter(filteredImage))
        finalImage = filteredImage.copy(Bitmap.Config.ARGB_8888, true)
    }

    private fun resetControl() {
//        editImageFragment.resetControl()
        brightnessFinal = 0
        constrantFinal = 1.0f
        saturationFinal = 1.0f
    }

    override fun onBrightnessChanged(brightness: Int) {
        brightnessFinal = brightness
        val myFilter = Filter()
        Log.e("bri", "ok")
        myFilter.addSubFilter(BrightnessSubFilter(brightness))
        image_preview.source.setImageBitmap(
            myFilter.processFilter(
                finalImage.copy(
                    Bitmap.Config.ARGB_8888,
                    true
                )
            )
        )
    }

    override fun onSaturationChanged(saturation: Float) {
        saturationFinal = saturation
        val myFilter = Filter()
        myFilter.addSubFilter(SaturationSubfilter(saturation))
        image_preview.source.setImageBitmap(
            myFilter.processFilter(
                finalImage.copy(
                    Bitmap.Config.ARGB_8888,
                    true
                )
            )
        )
    }

    override fun onConstrantChanged(constrant: Float) {
        constrantFinal = constrant
        val myFilter = Filter()
        myFilter.addSubFilter(ContrastSubFilter(constrant))
        image_preview.source.setImageBitmap(
            myFilter.processFilter(
                finalImage.copy(
                    Bitmap.Config.ARGB_8888,
                    true
                )
            )
        )
    }

    override fun onEditStarted() {

    }

    override fun onEditCompleted() {
        val bitmap = filteredImage.copy(Bitmap.Config.ARGB_8888, true)
        val myFilter = Filter()
        myFilter.addSubFilter(BrightnessSubFilter(brightnessFinal))
        myFilter.addSubFilter(SaturationSubfilter(saturationFinal))
        myFilter.addSubFilter(ContrastSubFilter(constrantFinal))
        finalImage = myFilter.processFilter(bitmap)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            else -> {
            }
        }
        if (id == R.id.action_open) {
            openImageFromGallery()
            return true
        } else if (id == R.id.action_save) {
            saveImageFromGallery()

            return true
        } else if (id == R.id.action_camera) {
            openCamera()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun openCamera() {
        val withListener = Dexter.withActivity(this)
            .withPermissions(
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {
                    if (p0!!.areAllPermissionsGranted()) {
                        val value = ContentValues()
                        value.put(MediaStore.Images.Media.TITLE, "New Picture")
                        value.put(MediaStore.Images.Media.DESCRIPTION, "From Camera")
                        imageUri = contentResolver.insert(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            value
                        )

                        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                        startActivityForResult(cameraIntent, CAMERA_REQUEST)
                    } else
                        Toast.makeText(applicationContext, "Permission denied", Toast.LENGTH_SHORT)
                            .show()
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: MutableList<PermissionRequest>?,
                    p1: PermissionToken?
                ) {
                    p1!!.continuePermissionRequest()
                }

            }).check()
    }

    private fun saveImageFromGallery() {
        val withListener = Dexter.withActivity(this)
            .withPermissions(
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {
                    if (p0!!.areAllPermissionsGranted()) {

                        photoEditor.saveAsBitmap(object : OnSaveBitmap {
                            override fun onBitmapReady(saveBitmap: Bitmap?) {
                                val pd = ProgressDialog(this@SuaAnhActivity)
                                pd.setTitle("Uploading")
                                pd.show()

                                val path = BitmapUtils.insertImage(
                                    contentResolver,
                                    saveBitmap,
                                    System.currentTimeMillis().toString() + "_profile.jpg",
                                    ""
                                )
                                val calendar = Calendar.getInstance()
                                val fileRef = storageReference?.child(
                                    "imagetotal/" + UUID.randomUUID().toString()
                                )
                                val baos: ByteArrayOutputStream = ByteArrayOutputStream()
                                saveBitmap!!.compress(Bitmap.CompressFormat.PNG, 100, baos)
                                val data = baos.toByteArray()

                                val uploadTask = fileRef!!.putBytes(data)
                                uploadTask.addOnFailureListener(object : OnFailureListener {
                                    override fun onFailure(p0: java.lang.Exception) {
                                        pd.dismiss()
                                        val snackBar = Snackbar.make(
                                            coordinator,
                                            "Upload Image Failed",
                                            Snackbar.LENGTH_LONG
                                        )
                                        snackBar.show()
                                    }
                                })
                                    .addOnSuccessListener(object :
                                        OnSuccessListener<UploadTask.TaskSnapshot?> {
                                        override fun onSuccess(p0: UploadTask.TaskSnapshot?) {
                                            pd.dismiss()
                                            val uri: Task<Uri> = p0!!.storage.downloadUrl
                                            val snackBar = Snackbar.make(
                                                coordinator,
                                                "Image Uploaded",
                                                Snackbar.LENGTH_LONG
                                            )
                                            writeNewFileImage(
                                                "IMG" + calendar.timeInMillis,
                                                uri.toString()
                                            )
                                            snackBar.show()
                                        }
                                    })
                                    .addOnProgressListener { p0 ->
                                        val progress: Double =
                                            (100.0 * p0.bytesTransferred) / p0.totalByteCount
                                        pd.setMessage("Upload ${progress.toInt()}%")
                                    }
                                if (!TextUtils.isEmpty(path)) {
                                    val snackBar = Snackbar.make(
                                        coordinator,
                                        "Image save to gallery",
                                        Snackbar.LENGTH_LONG
                                    )
                                        .setAction("OPEN") { openImage(path) }
                                    snackBar.show()
                                } else {
                                    val snackBar = Snackbar.make(
                                        coordinator,
                                        "Unable to save image",
                                        Snackbar.LENGTH_LONG
                                    )
                                    snackBar.show()
                                }
                            }

                            override fun onFailure(e: Exception?) {
                                val snackBar = Snackbar.make(
                                    coordinator,
                                    e!!.message.toString(),
                                    Snackbar.LENGTH_LONG
                                )
                                snackBar.show()
                            }

                        })
                    } else
                        Toast.makeText(applicationContext, "Permission denied", Toast.LENGTH_SHORT)
                            .show()
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: MutableList<PermissionRequest>?,
                    p1: PermissionToken?
                ) {
                    p1!!.continuePermissionRequest()
                }

            }).check()
    }

    private fun openImage(path: String?) {
        val intent = Intent()
        intent.action = Intent.ACTION_VIEW
        intent.setDataAndType(Uri.parse(path), "image/*")
        startActivity(intent)
    }

    private fun openImageFromGallery() {
        val withListener = Dexter.withActivity(this)
            .withPermissions(
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {
                    if (p0!!.areAllPermissionsGranted()) {
                        val intent = Intent(Intent.ACTION_PICK)
                        intent.type = "image/*"
                        startActivityForResult(intent, SELECT_GALLERY_PERMISSION)
                    } else
                        Toast.makeText(applicationContext, "Permission denied", Toast.LENGTH_SHORT)
                            .show()
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: MutableList<PermissionRequest>?,
                    p1: PermissionToken?
                ) {
                    p1!!.continuePermissionRequest()
                }

            }).check()
    }

    private fun writeNewFileImage(name: String, mUri: String) {
        val mImage = Image(name, mUri)
        alertWriteNote("note", name)
        val userId = mAuth!!.currentUser!!.uid
        val currentUserDb = databaseReference!!.child(userId).child("The Album").child("Total")
        currentUserDb.child(name)?.setValue(mImage)
    }

    private fun alertWriteNote(key: String, name: String) {

        val alertDialog2 = AlertDialog.Builder(this)
        alertDialog2.setTitle("Update Note")

        val linearLayout = LinearLayout(this)
        linearLayout.orientation = LinearLayout.VERTICAL
        linearLayout.setPadding(50, 10, 10, 10)

        val editText = EditText(this)
        editText.hint = "Write something about this image"

        linearLayout.addView(editText)
        alertDialog2.setView(linearLayout)
        alertDialog2.setPositiveButton("Update") { dialog, which ->
            val value = editText.text.toString().trim { it <= ' ' }
            val result = java.util.HashMap<String, Any>()
            result[key] = value
            val userId = mAuth!!.currentUser!!.uid
            val currentUserDb = databaseReference!!.child(userId).child("The Album").child("Total")
            currentUserDb!!.child(name).updateChildren(result)
                .addOnSuccessListener {
                    Toast.makeText(this, "Updated Note", Toast.LENGTH_SHORT).show()
                    onBackPressed()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Update Failed ", Toast.LENGTH_SHORT).show()
                    onBackPressed()
                }
        }

        alertDialog2.setNegativeButton("Cancel") { dialog, which ->
            Toast.makeText(this, "You clicked on Cancel", Toast.LENGTH_SHORT)
                .show()
            dialog.cancel()
            onBackPressed()
        }
        alertDialog2.create().show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == SELECT_GALLERY_PERMISSION) {
            val bitmap = BitmapUtils.getBitmapFromGallery(this, data?.data!!, 600, 800)
            originalImage!!.recycle()
            filteredImage.recycle()
            finalImage.recycle()

            originalImage = bitmap?.copy(Bitmap.Config.ARGB_8888, true)
            filteredImage = originalImage!!.copy(Bitmap.Config.ARGB_8888, true)
            finalImage = filteredImage.copy(Bitmap.Config.ARGB_8888, true)

            image_preview.source.setImageBitmap(originalImage)
            bitmap?.recycle()
            filterListFragment = FilterListFragment.getInstance(originalImage)
            filterListFragment.setListener(this)

        } else if (resultCode == Activity.RESULT_OK && requestCode == CAMERA_REQUEST) {
            val bitmap = BitmapUtils.getBitmapFromGallery(this, imageUri!!, 600, 800)

            originalImage!!.recycle()
            filteredImage.recycle()
            finalImage.recycle()

            originalImage = bitmap?.copy(Bitmap.Config.ARGB_8888, true)
            filteredImage = originalImage!!.copy(Bitmap.Config.ARGB_8888, true)
            finalImage = filteredImage.copy(Bitmap.Config.ARGB_8888, true)

            image_preview.source.setImageBitmap(originalImage)
            bitmap?.recycle()
            filterListFragment = FilterListFragment.getInstance(originalImage)
            filterListFragment.setListener(this)
        }
    }

    private val sensorListener: SensorEventListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]
            lastAcceleration = currentAcceleration
            currentAcceleration = sqrt((x * x + y * y + z * z).toDouble()).toFloat()
            val delta: Float = currentAcceleration - lastAcceleration
            acceleration = acceleration * 0.9f + delta
            if (acceleration > 12) {
                val now: Long = System.currentTimeMillis();
                // ignore shake events too close to each other (500ms)
                var mShakeTimestamp: Long? = null
                var mShakeCount = 1
                if (mShakeTimestamp != null) {
                    if (mShakeTimestamp + SHAKE_SLOP_TIME_MS > now) {
                        return;
                    }

                    if (mShakeTimestamp + SHAKE_COUNT_RESET_TIME_MS < now){
                        mShakeCount = 0
                    }
                }
                mShakeTimestamp = now
                mShakeCount++

                if (Round(x, 4) < -30.0000) {

                    val bitmap = filteredImage.copy(Bitmap.Config.ARGB_8888, true)

                    bitmap.apply {
                        val imageView = findViewById<PhotoEditorView>(R.id.image_preview)
                        bitmap.apply {
                            val imageView = findViewById<PhotoEditorView>(R.id.image_preview)
                            imageView.source.setImageBitmap(this!!.rotate(-90F))
                        }
                        Toast.makeText(applicationContext, "Rotate Right", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else if (Round(x, 4) > 30.0000) {
                    val bitmap = filteredImage.copy(Bitmap.Config.ARGB_8888, true)

                    bitmap.apply {
                        val imageView = findViewById<PhotoEditorView>(R.id.image_preview)
                        imageView.source.setImageBitmap(this!!.rotate(90F))
                    }
                    Toast.makeText(applicationContext, "Rotate Left", Toast.LENGTH_SHORT)
                        .show()
                }else if (Round(y, 4)< -30.0000) {
                    val bitmap = filteredImage.copy(Bitmap.Config.ARGB_8888, true)

                    bitmap.apply {
                        val imageView = findViewById<PhotoEditorView>(R.id.image_preview)
                        imageView.source.setImageBitmap(this!!.rotate(-180F))
                    }
                    Toast.makeText(applicationContext, "Rotate Up", Toast.LENGTH_SHORT)
                        .show()
                }else if (Round(y, 4)< 30.0000) {
                val bitmap = filteredImage.copy(Bitmap.Config.ARGB_8888, true)
                bitmap.apply {
                    val imageView = findViewById<PhotoEditorView>(R.id.image_preview)
                    imageView.source.setImageBitmap(this!!.rotate(0F))
                }
                Toast.makeText(applicationContext, "Rotate Down", Toast.LENGTH_SHORT)
                    .show()
                }
            }
        }

        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
    }

    override fun onResume() {
        super.onResume()
        // Add the following line to register the Session Manager Listener onResume
        mSensorManager?.registerListener(
            sensorListener, mSensorManager!!.getDefaultSensor(
                Sensor.TYPE_ACCELEROMETER
            ), SensorManager.SENSOR_DELAY_NORMAL
        )
    }

    override fun onPause() {
        // Add the following line to unregister the Sensor Manager onPause
        mSensorManager?.unregisterListener(sensorListener)
        super.onPause()
    }


    //Làm tròn x
    fun Round(Rval: Float, Rpl: Int): Float {
        var Rval = Rval
        val p = 10.0.pow(Rpl.toDouble()).toFloat()
        Rval *= p
        val tmp = Rval.roundToInt().toFloat()
        return tmp / p
    }
    

    //Xoay bitmap
    fun Bitmap.rotate(angle: Float = 0F): Bitmap? {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(this, 0, 0, width, height, matrix, false)
    }


    override fun onIconItemSelected(icon: String) {
        photoEditor.addEmoji(icon)
    }

    override fun onAddTextListener(text: String, color: Int) {
        photoEditor.addText(text, color)
    }
}
