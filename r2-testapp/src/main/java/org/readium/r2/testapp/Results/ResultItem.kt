package org.readium.r2.testapp.Results

import org.json.JSONObject
import java.io.Serializable

class ResultItem(var reference: String = "", var id : Int = 0, var snippet: String ="") : Serializable{
    fun ResultItem(ref: String, i : Int, snip: String) {
        reference = ref
        id = i
        snippet = snip
    }
}