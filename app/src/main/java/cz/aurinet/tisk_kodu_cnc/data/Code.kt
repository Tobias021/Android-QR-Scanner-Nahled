package cz.aurinet.tisk_kodu_cnc.data

import java.util.regex.Pattern

data class Code(val code: String){
    private var parsedCode: Array<String> = code.split(Pattern.quote("|")).toTypedArray()
    private var procedureName: String = if(parsedCode.size > 1) {
        (if (parsedCode.size == 3) parsedCode[1] + " " + parsedCode[2] else parsedCode[1])
    } else {
        code
    }

    fun getProcedureName(): String{
        return procedureName
    }
}
