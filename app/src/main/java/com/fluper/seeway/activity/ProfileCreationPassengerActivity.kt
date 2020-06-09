package com.fluper.seeway.activity

import android.app.Activity
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.Dialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.Window
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.fluper.seeway.R
import com.fluper.seeway.fragment.ChosseSecurityFragment
import com.google.android.material.textfield.TextInputLayout
import com.rilixtech.CountryCodePicker
import kotlinx.android.synthetic.main.activity_profile_creation_passenger.*
import java.text.SimpleDateFormat
import java.util.*


class ProfileCreationPassengerActivity : AppCompatActivity() {
    private var yeaR: Int = 0
    private var month: Int = 0
    private var day: Int = 0
    private lateinit var cal: Calendar
    private val IMAGE_PICK_CODE = 1000;
    private val PERMISSION_CODE1 = 1000;
    private val IMAGE_CAPTURE_CODE = 1001
    var image_uri: Uri? = null
    private val PERMISSION_CODE = 1001;
    lateinit var btn_skip_profile: TextView
    lateinit var btn_save: Button
    lateinit var profile_image: ImageView
    lateinit var profile_image1: ImageView
    lateinit var img_cam: ImageView
    lateinit var linear_card_date: LinearLayout
    var ccp: CountryCodePicker? = null
    var dateSelected: Calendar = Calendar.getInstance()
    private var datePickerDialog: DatePickerDialog? = null


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_creation_passenger)
//click kh
        edt_card_date.isEnabled = false
        edt_card_date.isClickable = false
        edt_card_date.isFocusable = false
        btn_skip_profile = findViewById(R.id.btn_skip_profile)
        linear_card_date = findViewById(R.id.linear_card_date)
        btn_save = findViewById(R.id.btn_save)
        profile_image = findViewById(R.id.profile_image)
        profile_image1 = findViewById(R.id.profile_image1)
        img_cam = findViewById(R.id.img_cam)
        ccp  = findViewById(R.id.ccp)
        val type = Typeface.createFromAsset(getAssets(),"font/avenir_black.ttf");
        (ccp as CountryCodePicker).setTypeFace(type)

        txt_card_expire
        //yha kro ok
        btn_skip_profile.setOnClickListener {

            val dialog = this?.let { it1 -> Dialog(it1) }
            dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog?.setCancelable(false)
            dialog?.setContentView(R.layout.alert_profile)

            val btn_skip = dialog?.findViewById<Button>(R.id.btn_skip) as TextView

            val btn_create_profile = dialog.findViewById<View>(R.id.btn_create_profile) as Button
            btn_create_profile.setOnClickListener { dialog.dismiss() }


            btn_skip.setOnClickListener {
                setFragment(ChosseSecurityFragment())

                dialog?.dismiss()
            }

            dialog?.dismiss()
            dialog.show()
        }

        btn_save.setOnClickListener {
            val i = Intent(this, HomeScreenNavPassengerActivity::class.java)
            startActivity(i)

        }
        //Ye hi h na

        view_relative.setOnClickListener {


            val calendar = Calendar.getInstance()

            val datePickerDialog = DatePickerDialog(
                this,
                R.style.DatePickerDialogTheme,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    val newDate = Calendar.getInstance()
                    newDate[year, monthOfYear] = dayOfMonth
                    val simpleDateFormat =
                        SimpleDateFormat("dd-MM-yyyy")
                    val date = simpleDateFormat.format(newDate.time)
                    edt_card_date.setText(date)
                },
                calendar[Calendar.YEAR],
                calendar[Calendar.MONTH],
                calendar[Calendar.DAY_OF_MONTH]
            )

            datePickerDialog.show()

        }

        img_cam.setOnClickListener {    val dialog = this?.let { it1 -> Dialog(it1) }
            dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog?.setCancelable(true)
            dialog?.setContentView(R.layout.open_cemera)

            val btn_cemera = dialog?.findViewById<Button>(R.id.btn_cemera) as TextView

            val btn_gellery = dialog.findViewById<View>(R.id.btn_gellery) as Button
            btn_gellery.setOnClickListener {
                if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_DENIED
                ) {
                    //permission denied
                    val permissions = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE);
                    //show popup to request runtime permission
                    requestPermissions(permissions, PERMISSION_CODE)
                    dialog?.dismiss()
                } else {
                    //permission already granted
                    pickImageFromGallery();
                    dialog?.dismiss()
                }

                dialog?.dismiss()
            }

            btn_cemera.setOnClickListener {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(android.Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_DENIED ||
                        checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_DENIED
                    ) {
                        //permission was not enabled
                        val permission = arrayOf(
                            android.Manifest.permission.CAMERA,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                        )
                        //show popup to request permission
                        requestPermissions(permission, PERMISSION_CODE)
                        dialog?.dismiss()
                    } else {
                        //permission already granted
                        openCamera()
                        dialog?.dismiss()
                    }
                } else {
                    //system os is < marshmallow
                    openCamera()
                    dialog?.dismiss()
                }
                dialog?.dismiss()

            }
            dialog.show()
        }

        profile_image.setOnClickListener {
            val dialog = this?.let { it1 -> Dialog(it1) }
            dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog?.setCancelable(true)
            dialog?.setContentView(R.layout.open_cemera)

            val btn_cemera = dialog?.findViewById<Button>(R.id.btn_cemera) as TextView

            val btn_gellery = dialog.findViewById<View>(R.id.btn_gellery) as Button
            btn_gellery.setOnClickListener {
                if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_DENIED
                ) {
                    //permission denied
                    val permissions = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE);
                    //show popup to request runtime permission
                    requestPermissions(permissions, PERMISSION_CODE)
                    dialog?.dismiss()
                } else {
                    //permission already granted
                    pickImageFromGallery();
                    dialog?.dismiss()
                }

                dialog?.dismiss()
            }

            btn_cemera.setOnClickListener {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(android.Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_DENIED ||
                        checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_DENIED
                    ) {
                        //permission was not enabled
                        val permission = arrayOf(
                            android.Manifest.permission.CAMERA,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                        )
                        //show popup to request permission
                        requestPermissions(permission, PERMISSION_CODE)
                        dialog?.dismiss()
                    } else {
                        //permission already granted
                        openCamera()
                        dialog?.dismiss()
                    }
                } else {
                    //system os is < marshmallow
                    openCamera()
                    dialog?.dismiss()
                }
                dialog?.dismiss()

            }
            dialog.show()
        }

    }



    protected fun setFragment(fragment: Fragment?) {
        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        if (fragment != null) {
            val args = Bundle()
            args.putString("profile", "passenger")
            fragment.setArguments(args)
            fragmentTransaction.add(android.R.id.content, fragment)
        }
        fragmentTransaction
            .addToBackStack(null).commit()
    }


    private fun pickImageFromGallery() {
        //Intent to pick image
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }
private fun openCamera() {
    val values = ContentValues()
    values.put(MediaStore.Images.Media.TITLE, "New Picture")
    values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera")
    image_uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
    //camera intent
    val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri)
    startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE)
}
    companion object {
        //image pick code
        private val IMAGE_PICK_CODE = 1000;
        //Permission code
        private val PERMISSION_CODE = 1001;
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){
            PERMISSION_CODE -> {
                if (grantResults.size >0 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED){
                    //permission from popup granted
                    pickImageFromGallery()
                }
                else{
                    //permission from popup denied
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
            PERMISSION_CODE1 -> {
                if (grantResults.size >0 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED){
                    //permission from popup granted
                    openCamera()
                }
                else{
                    //permission from popup denied
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }

        }


    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE){
            profile_image.setImageURI(data?.data)
        }

        if (resultCode == Activity.RESULT_OK&& requestCode == IMAGE_CAPTURE_CODE){

            profile_image.setImageURI(image_uri)
        }
    }

//    override fun onClick(v: View) {
//        if (v === edt_card_date) {
//
//            edt_card_date.setFocusableInTouchMode(false)
//            edt_card_date.setFocusable(false)
//            edt_card_date.setTextIsSelectable(true)
//            val calendar = Calendar.getInstance()
//
//            val datePickerDialog = DatePickerDialog(
//                this,
//                R.style.DatePickerDialogTheme,
//                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
//                    val newDate = Calendar.getInstance()
//                    newDate[year, monthOfYear] = dayOfMonth
//                    val simpleDateFormat =
//                        SimpleDateFormat("dd-MM-yyyy")
//                    val date = simpleDateFormat.format(newDate.time)
//                    edt_card_date.setText(date)
//                },
//                calendar[Calendar.YEAR],
//                calendar[Calendar.MONTH],
//                calendar[Calendar.DAY_OF_MONTH]
//            )
//
//            datePickerDialog.show()
//        }
//    }

}


