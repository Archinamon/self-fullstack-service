package me.archinamon.web.json.serialize

import pl.treksoft.jquery.JQueryAjaxSettings
import pl.treksoft.jquery.JQueryXHR

typealias BeforeExecCall = (JQueryXHR, JQueryAjaxSettings) -> Boolean

interface CallExecCallback {
    var beforeSend: BeforeExecCall?

    fun onBeforeCallExecutes(callback: BeforeExecCall) {
        beforeSend = callback
    }
}