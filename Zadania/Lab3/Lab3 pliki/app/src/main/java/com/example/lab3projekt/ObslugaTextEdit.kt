package com.example.lab3projekt

class ObslugaTextEdit {
    companion object {
        fun obslugaEditPassword(password: String): String {
            return if (password.length < 8) "Hasło za krótkie!" else "Super hasło!"
        }

        fun obslugaEditEmail(email: String): String {
            var warnEmail = "Niepoprawny e-mail!"
            for (i in email) {
                if (i == '@') {
                    warnEmail = "Poprawny e-mail!"
                    break
                }
            }
            return warnEmail
        }
    }
}
