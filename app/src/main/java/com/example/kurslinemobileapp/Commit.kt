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
