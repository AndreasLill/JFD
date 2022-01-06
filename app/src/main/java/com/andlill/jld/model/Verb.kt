package com.andlill.jld.model

class Verb {

    enum class Group {
        Ichidan,
        Godan,
        Irregular,
    }

    var present : String = ""
    var past : String = ""
    var teForm : String = ""
    var potential : String = ""
    var passive : String = ""
    var causative : String = ""
    var imperative : String = ""
}