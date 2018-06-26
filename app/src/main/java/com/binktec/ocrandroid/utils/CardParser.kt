package com.binktec.ocrandroid.utils

import com.binktec.ocrandroid.data.model.Entities
import java.util.regex.Pattern


class CardParser constructor(private val text:String, private val entittes:List<Entities>){
    var email = "somemail@yopmail.com"
    var number = "9999999999"
    var name = ""
    var place = ""
    var company = ""

    fun parse() {
        val lines = text.split("\n")
        for (line in lines) {
            val words = line.split(" ")
            for (word in words) {
                if (checkEmail(word)) email += "$word "
                if (checkNumber(word)) number = word
            }
        }
        for (entity in entittes) {
            for (t in entity.type.orEmpty()) {
                if (t == "Person") {
                    name += entity.entityId.orEmpty() + " "
                    break
                }
                if (t == "Company" || t == "Organisation") {
                    company += entity.entityId.orEmpty() + " "
                    break
                }
                if (t == "Place" || t == "City" || t == "Country") {
                    place += entity.entityId.orEmpty() + " "
                }
            }
        }
    }

    private fun checkEmail(string:String): Boolean {
        val emailPattern = Pattern.compile("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)])")
        if (string.isNotEmpty()) return false
        return emailPattern.matcher(string).matches()
    }

    private fun checkNumber(string:String): Boolean {
        val n = string.toLongOrNull()
        return (n != null && string.length==10)
    }


}