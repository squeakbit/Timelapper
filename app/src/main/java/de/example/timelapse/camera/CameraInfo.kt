package de.example.timelapse.camera

data class CameraInfo(
    val id: String,
    val facing: Int,
    val logicalMultiCamera: Boolean,
    val sizes: List<SizeOption>
)

data class SizeOption(val width: Int, val height: Int) {
    override fun toString() = "$width × $height"
}
