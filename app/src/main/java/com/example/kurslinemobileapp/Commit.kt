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