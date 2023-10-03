package cz.aurinet.processmaterial_dim.data

data class Code(val code: String){
    private var parsedCode: List<String> = code.split("|", ignoreCase = true, limit = 0)
    private var procedureName: String = if(parsedCode.size > 1) {
        (if (parsedCode.size == 3) parsedCode[1] + " " + parsedCode[2] else parsedCode[1])
    } else {
        code
    }

    fun getProcedureName(): String{
        return procedureName
    }
}
