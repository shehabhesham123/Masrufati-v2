package com.example.masrufati

import android.annotation.SuppressLint
import android.util.Log
import java.text.SimpleDateFormat

class Date {
    companion object{
        @SuppressLint("SimpleDateFormat")
        fun currentDate(): String {
            return SimpleDateFormat("dd/MM/yyyy").format(java.util.Date())
        }

        fun difference(date1: String, date2: String): Int {      // return how many days
            val day1 = "${date1[0]}${date1[1]}".toInt()
            val month1 = "${date1[3]}${date1[4]}".toInt()
            val year1 = "${date1[6]}${date1[7]}${date1[8]}${date1[9]}".toInt()
            var day2 = "${date2[0]}${date2[1]}".toInt()
            var month2 = "${date2[3]}${date2[4]}".toInt()
            var year2 = "${date2[6]}${date2[7]}${date2[8]}${date2[9]}".toInt()
            if (day2 < day2) {
                day2 += if (month2 == 2) {
                    if (isLeapYear(year2)) 29
                    else 28
                } else if (month2 == 1 || month2 == 3 || month2 == 5 || month2 == 7 || month2 == 10 || month2 == 12) 31
                else 30
                month2 -= 1
            }
            if (month2 < month1) {
                month2 += 12
                year2 -= 1
            }
            val days =(day2 - day1) + ((month2 - month1) * 30) + ((year2 - year1) * 365)
            if( days >= 0) return days
            throw Exception("- date")
        }

        private fun isLeapYear(year: Int): Boolean {
            return (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0))
        }
    }
}