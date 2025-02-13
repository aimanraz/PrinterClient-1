package net.nyx.printerservice.print

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PrintTextFormat(
    // Text size in px, default 24
    var textSize: Int = 24,
    
    // Whether text has underline
    var isUnderline: Boolean = false,
    
    // Horizontal scale factor, default 1.0. Values > 1.0 stretch wider, < 1.0 stretch narrower
    var textScaleX: Float = 1.0f,
    
    // Vertical scale factor, default 1.0
    var textScaleY: Float = 1.0f,
    
    // Letter spacing in 'EM' units, default 0
    var letterSpacing: Float = 0f,
    
    // Line spacing
    var lineSpacing: Float = 0f,
    
    // Top padding
    var topPadding: Int = 0,
    
    // Left padding
    var leftPadding: Int = 0,
    
    // Text alignment: 0--LEFT, 1--CENTER, 2--RIGHT
    var ali: Int = 0,
    
    // Text style: 0--NORMAL, 1--BOLD, 2--ITALIC, 3--BOLD_ITALIC
    var style: Int = 0,
    
    // Font type: 0--DEFAULT, 1--DEFAULT_BOLD, 2--SANS_SERIF, 3--SERIF, 4--MONOSPACE, 5--CUSTOM
    var font: Int = 0,
    
    // Custom font file path
    var path: String? = null
) : Parcelable 