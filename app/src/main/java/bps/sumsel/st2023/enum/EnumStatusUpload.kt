package bps.sumsel.st2023.enum

enum class EnumStatusUpload(val kode: Int) {
    NOT_UPLOADED(0),
    UPLOADED(1),
    CHANGED_AFTER_UPLOADED(2),
    DELETED_AFTER_UPLOADED(3)
}