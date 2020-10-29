package com.example.noteapp.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.noteapp.R
import com.example.noteapp.SuaAnhActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*


class ImageActivity : AppCompatActivity() {

    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null
    private var mAuth: FirebaseAuth? = null
    private var fbUser: FirebaseUser? = null

    private var img_select: ImageView? = null
    private var ln: LinearLayout? = null
    private var btn_fiximage: Button? = null
    private var btn_delimage: Button? = null
    private var btn_infoimage: Button? = null
    private var btn_setava: Button? = null

    var image : Bitmap? = null
//    var permission = arrayOf(
//        Manifest.permission.READ_EXTERNAL_STORAGE,
//        Manifest.permission.WRITE_EXTERNAL_STORAGE
//    )
    val PERMISSION_WRITE = 0
    var fileUri : String? = null

    private var mSensorManager : SensorManager?= null
    private var acceleration = 0f
    private var currentAcceleration = 0f
    private var lastAcceleration = 0f

    private val SHAKE_SLOP_TIME_MS = 999999
    private val SHAKE_COUNT_RESET_TIME_MS = 5000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)
        ActionBarCustom()

        mAuth = FirebaseAuth.getInstance()
        fbUser = mAuth!!.currentUser
        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference.child("Users")

        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        Objects.requireNonNull(mSensorManager)!!.registerListener(
            sensorListener, mSensorManager!!
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL
        )
        acceleration = 10f
        currentAcceleration = SensorManager.GRAVITY_EARTH
        lastAcceleration = SensorManager.GRAVITY_EARTH

        val storageRef =
            FirebaseStorage.getInstance().getReference(fbUser!!.uid).child("imagetotal/")

        var a = false
        img_select = findViewById(R.id.img_select)
        ln = findViewById(R.id.layout_bot)
        btn_fiximage = findViewById(R.id.btn_fiximage)
        btn_delimage = findViewById(R.id.btn_delimage)
        btn_infoimage = findViewById(R.id.btn_infoimage)
        btn_setava = findViewById(R.id.btn_setava)
        hideLayout(a)
        var intent1: Intent

        getIncomingIntent()

        // Nhận link ảnh trong storage
//        val url : String? = intent.getStringExtra("url")
//        Picasso.get().load(url).into(img_select)
        val setava = intent.getBooleanExtra("setava",false)
        if (setava){
            setAvatar()
            intent1 = Intent(this,MainActivity::class.java)
            intent1.putExtra("setava",true)
            startActivity(intent1)
        }


        img_select!!.setOnClickListener {
            if (!a) {
                hideLayout(true)
                a = true
            } else {
                hideLayout(false)
                a = false
            }
        }

        btn_fiximage!!.setOnClickListener {
            // Truyền url sang SuaAnhActivity
            val imageUrl: String = intent.getStringExtra("image_url").toString()

            intent1 = Intent(this, SuaAnhActivity::class.java)
            intent1.putExtra("a", false)
            intent1.putExtra("url", imageUrl)
            startActivity(intent1)
        }

        btn_delimage!!.setOnClickListener {
            deleteImage()
        }

        checkPermission()

        btn_infoimage!!.setOnClickListener {
            if (checkPermission()){
                val imageUrl: String = intent.getStringExtra("image_url").toString()
                imageShare(imageUrl)
            }
        }

        btn_setava!!.setOnClickListener {
            setAvatar()
            intent1 = Intent(this,MainActivity::class.java)
            intent1.putExtra("setava",true)
            startActivity(intent1)
        }
    }

    fun getIncomingIntent() {
        if (intent.hasExtra("image_url")) {
            val imageUrl: String = intent.getStringExtra("image_url").toString()

            val imageView = findViewById<ImageView>(R.id.img_select)
            Glide.with(this)
                .asBitmap()
                .load(imageUrl)
                .into(imageView)
        }
    }

    fun deleteImage() {
        val imageUrl: String = intent.getStringExtra("image_url").toString()

        val alertDialogDelete = android.app.AlertDialog.Builder(this)
        alertDialogDelete.setTitle("Do you want to delete this image?")

        alertDialogDelete.setPositiveButton("Yes") { dialog, which ->
            val storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl)

            storageRef.delete().addOnSuccessListener {
                startActivity(Intent(this, ListImageActivity::class.java))
                Toast.makeText(this, "Image Is Deleted", Toast.LENGTH_SHORT).show()

            }.addOnFailureListener {
                Toast.makeText(this, "Something's Wrong! Try Again!", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }.setNegativeButton("Cancel") { dialog, which -> dialog.dismiss() }
        alertDialogDelete.show()
    }

    fun setAvatar() {
        val imageUrl: String = intent.getStringExtra("image_url").toString()

        mDatabaseReference!!.child(fbUser!!.uid).child("The Album").child("User Avatar")
            .setValue(
                imageUrl
            )
            .addOnSuccessListener {
//                val fragment = UserFragment()
//                supportFragmentManager.beginTransaction().add(R.id.fram, fragment, "2")
//                    .addToBackStack(null).commitNow()
                Toast.makeText(this, "Updated New Avatar", Toast.LENGTH_SHORT).show()

            }
            .addOnFailureListener {
                Toast.makeText(this, "Update Failed ", Toast.LENGTH_SHORT).show()
            }
    }

    fun imageShare(url: String){

        Picasso.get().load(url).into(object : com.squareup.picasso.Target {
            override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                try {
                    val mydir: File = File(
                        Environment.getExternalStorageDirectory().toString() + "/11zon"
                    )
                    if (!mydir.exists()) {
                        mydir.mkdirs()
                    }
                    fileUri = mydir.getAbsolutePath() + File.separator + System.currentTimeMillis()
                        .toString() + ".jpg"
                    val outputStream = FileOutputStream(fileUri)
                    bitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                    outputStream.flush()
                    outputStream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                val uri: Uri = Uri.parse(
                    MediaStore.Images.Media.insertImage(
                        contentResolver,
                        BitmapFactory.decodeFile(fileUri),
                        null,
                        null
                    )
                )
                val intent: Intent = Intent(Intent.ACTION_SEND)
                intent.type = "image/*"
                intent.putExtra(Intent.EXTRA_STREAM, uri)
                startActivity(Intent.createChooser(intent, "Share Image"))
            }

            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}
            override fun onBitmapFailed(e: java.lang.Exception?, errorDrawable: Drawable?) {}
        })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 100){
            when{
                grantResults.isNotEmpty()
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ->{
                    val imageUrl: String = intent.getStringExtra("image_url").toString()
                    val intent = Intent(Intent.ACTION_SEND)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    intent.putExtra(Intent.EXTRA_STREAM, imageUrl)
                    intent.type = "image/png"
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Subject here")
                    startActivity(Intent.createChooser(intent, "Share Image via"))
                }
            }
            return
        }
    }

    fun checkPermission(): Boolean {
        val READ_EXTERNAL_PERMISSION =
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        if (READ_EXTERNAL_PERMISSION != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                PERMISSION_WRITE
            )
            return false
        }
        return true
    }


    private fun hideLayout(a: Boolean) {
        if (a)
            ln!!.visibility = View.VISIBLE
        else
            ln!!.visibility = View.INVISIBLE

    }

    private val sensorListener: SensorEventListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]
            lastAcceleration = currentAcceleration
            currentAcceleration = Math.sqrt((x * x + y * y + z * z).toDouble()).toFloat()
            val delta: Float = currentAcceleration - lastAcceleration
            acceleration = acceleration * 0.9f + delta
            if (acceleration > 12) {

                var now : Long = System.currentTimeMillis();
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

                if (Round(x,4)< -30.0000){
                    val imageView = findViewById<ImageView>(R.id.img_select)
                    imageView.rotation = imageView.rotation - 90
                    Toast.makeText(applicationContext, "Shake it Right", Toast.LENGTH_SHORT).show()
                }else if (Round(x,4)>30.0000) {
                    val imageView = findViewById<ImageView>(R.id.img_select)
                    imageView.rotation = imageView.rotation + 90
                    Toast.makeText(applicationContext, "Shake it Left", Toast.LENGTH_SHORT).show()
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
    fun Round(Rval: Float, Rpl: Int): Float {
        var Rval = Rval
        val p = Math.pow(10.0, Rpl.toDouble()).toFloat()
        Rval = Rval * p
        val tmp = Math.round(Rval).toFloat()
        return tmp / p
    }

    private fun ActionBarCustom(){
        supportActionBar?.title =""
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            else -> {
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
