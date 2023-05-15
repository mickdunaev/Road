package com.app.road.model

class Meditation(
    val title: String,
    val id: Long,
    val mode: Long,
    val link: String,
    val duration: String,
) {

    var isAboutCourseButton: Boolean = false
    var pay: Boolean = false
    var isKuplena: Boolean = false
    constructor(
        title: String,
        id: Long,
        mode: Long,
        link: String,
        duration: String,
        courseButton: Boolean
    ) : this(
        title,
        id,
        mode,
        link,
        duration
    ) {
        isAboutCourseButton = courseButton
    }

}