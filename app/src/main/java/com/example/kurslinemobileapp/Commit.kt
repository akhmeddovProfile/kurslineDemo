package com.example.kurslinemobileapp

/*
val mainpageFragment=MainPageFragment()
val registrationpageFragment=RegisterFragment()
val favoritesFragment=FavoritePageFragment()

makeCurrentFragment(mainpageFragment)

binding.bottomNav.setOnNavigationItemSelectedListener {
    when(it.itemId){
        R.id.mainPageFragment->makeCurrentFragment(mainpageFragment)
        R.id.accountFragment->makeCurrentFragment(registrationpageFragment)
        R.id.favorites->makeCurrentFragment(favoritesFragment)
    }
    true
}

 */
/*
/*        val username = emailLoginEditText.text.toString()
        val password = passwordLoginEditText.text.toString()*/
sharedPreferences.edit().putString("user_type", response.).apply()
        sharedPreferences.edit().putString("username", username).apply()
        sharedPreferences.edit().putString("password", password).apply()
        sharedPreferences.edit().putString("company", company).apply()
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        startActivity(intent)
        finish()*/
/*
/*     fun selectCertificate(view: View){

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Snackbar.make(view, "Permission needed for gallery", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission",
                    View.OnClickListener {
                        permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    }).show()
            } else {
                permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        } else {
            val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            activityResultLauncher.launch(intentToGallery)
        }
    }

     fun downloadPhotoFromGalery() {
        activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intentFromResult = result.data
                if (intentFromResult != null) {
                    selectedPicture = intentFromResult.data
                    try {
                        if (Build.VERSION.SDK_INT >= 28) {
                            val source = ImageDecoder.createSource(
                                this.contentResolver,
                                selectedPicture!!
                            )
                            selectedBitmap = ImageDecoder.decodeBitmap(source)

                            certificateImage.setImageBitmap(selectedBitmap)
                        } else {
                            selectedBitmap = MediaStore.Images.Media.getBitmap(
                                this.contentResolver,
                                selectedPicture
                            )
                            certificateImage.setImageBitmap(selectedBitmap)
                        }
                        val cervicatePath = getPathFromURI(selectedPicture)
                        companyPhoto.setText(cervicatePath!!.substring((cervicatePath.length - 8) , cervicatePath.length)+".JPG")
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }
        permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { result ->
            if (result) {
                //permission granted
                val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            } else {
                //permission denied
                Toast.makeText(this@RegisterCompanyActivity, "Permisson needed!", Toast.LENGTH_LONG).show()
            }
        }
    }*/

        val language = sharedPreferences.getString("language", "DEFAULT")
        if(language.equals("av")){
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Language Selected")
            builder.setMessage("You have selected 'Azerbaijan' language.")
            builder.setPositiveButton("OK") { dialog: DialogInterface, _: Int ->
                dialog.dismiss()
            }
            val alertDialog = builder.create()
            alertDialog.show()
        }
        if (language.equals("en")){
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Language Selected")
            builder.setMessage("You have selected 'English' language.")
            builder.setPositiveButton("OK") { dialog: DialogInterface, _: Int ->
                dialog.dismiss()
            }
            val alertDialog = builder.create()
            alertDialog.show()
        }
*/
/*
    private fun setLocale(activity: Activity, languageCode: String?) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val resources: Resources = activity.resources
        val config: Configuration = resources.getConfiguration()
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.getDisplayMetrics())
    }
*/
/*    @SuppressLint("NewApi")
    fun getPathFromURI(uri: Uri?): String? {
        var filePath = ""
        val wholeID = DocumentsContract.getDocumentId(uri)

        // Split at colon, use second item in the array
        val id = wholeID.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
        val column = arrayOf(MediaStore.Images.Media.DATA)

        // where id is equal to
        val sel = MediaStore.Images.Media._ID + "=?"
        val cursor: Cursor? = this.getContentResolver().query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            column, sel, arrayOf<String>(id), null
        )
        val columnIndex = cursor?.getColumnIndex(column[0])
        if (cursor!!.moveToFirst()) {
            filePath = cursor.getString(columnIndex!!)
        }
        cursor.close()
        return filePath
    }*/
/*    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_EXTERNAL_STORAGE && resultCode == RESULT_OK) {
            var path: String? = null
            val clipData = data!!.clipData
            if (clipData != null) {
                for (i in 0 until clipData.itemCount) {
                    val imageUri = clipData.getItemAt(i).uri
                    Log.d("URI", imageUri.path!!)
                    try {
                        val inputStream: InputStream? =
                            this.getContentResolver().openInputStream(imageUri)
                        // Static.sellectImageIsChange = true;

                        path =getPathFromURI(imageUri)

                        Log.d("TAG", "File Path: $path")
                    } catch (e: FileNotFoundException) {
                        e.printStackTrace()
                    }
                }
            } else {
                val imageUri = data.data
                try {
                    val inputStream: InputStream? =
                        this.getContentResolver().openInputStream(imageUri!!)
                    path = getPathFromURI(imageUri)
                    companyPhoto.setText(path!!)
                    //                    Static.sellectImageIsChange = true;
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                }
            }
        }
    }*/


