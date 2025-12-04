package com.example.smartpill_wearos.presentation.utils

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter

object QRCodeUtils {

    fun generateQRCode(content: String, width: Int = 512, height: Int = 512): Bitmap? {
        return try {
            val writer = QRCodeWriter()
            // Cria a matriz de bits (0 e 1) representando o QR Code
            val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, width, height)

            val w = bitMatrix.width
            val h = bitMatrix.height
            val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565)

            // Pinta os pixels: Preto para 1, Branco para 0
            for (x in 0 until w) {
                for (y in 0 until h) {
                    bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                }
            }
            bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}