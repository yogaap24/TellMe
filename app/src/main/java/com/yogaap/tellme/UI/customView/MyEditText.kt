package com.yogaap.tellme.UI.customView

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns
import com.google.android.material.textfield.TextInputEditText

class MyEditText : TextInputEditText {

    private var errorBackground: Drawable? = null
    private var defaultBackground: Drawable? = null
    private var isError: Boolean = false

    companion object {
        const val EMAIL = 0x00000021
        const val PASSWORD = 0x00000081
    }

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        background = if (isError) {
            errorBackground
        } else {
            defaultBackground
        }
    }

    private fun init() {
        errorBackground = context.getDrawable(com.yogaap.tellme.R.drawable.bg_edt_error)
        defaultBackground = context.getDrawable(com.yogaap.tellme.R.drawable.bg_edt_default)

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val input = p0.toString()
                when (inputType) {
                    EMAIL -> {
                        if (!Patterns.EMAIL_ADDRESS.matcher(input).matches()) {
                            error = context.getString(com.yogaap.tellme.R.string.email_validation)
                            isError = true
                        } else {
                            isError = false
                        }
                    }

                    PASSWORD -> {
                        if (input.length < 8) {
                            error =
                                context.getString(com.yogaap.tellme.R.string.password_length)
                            isError = true
                        } else {
                            isError = false
                        }
                    }
                }
            }

            override fun afterTextChanged(p0: Editable?) {
                val input = p0.toString()
                when (inputType) {
                    EMAIL -> {
                        if (!Patterns.EMAIL_ADDRESS.matcher(input).matches()) {
                            error = context.getString(com.yogaap.tellme.R.string.email_validation)
                            isError = true
                        } else {
                            isError = false
                        }
                    }

                    PASSWORD -> {
                        if (input.length < 8) {
                            error =
                                context.getString(com.yogaap.tellme.R.string.password_length)
                            isError = true
                        } else {
                            isError = false
                        }
                    }
                }
            }
        })
    }
}