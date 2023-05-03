package bps.sumsel.st2023.enum

enum class EnumSubsektor(val label: String,  val code: Int) {
    PanganPadi("Pangan Padi", 1),
    PanganPalawija("Pangan Palawija", 2),
    HortikulturaTahunan("Hortikultura Tahunan", 3),
    HortikulturaSemusim("Hortikultura Semusim", 4),
    PerkebunanTahunan("Perkebunan Tahunan", 5),
    PerkebunanSemusim("Perkebunan Semusim", 6),
    PeternakanBesar("Peternakan Besar", 7),
    PeternakanSedang("Peternakan Sedang", 8),
    PeternakanUnggas("Peternakan Unggas", 9),
    PerikananBudidaya("Perikanan Budidaya", 10),
    PerikananTangkapUmum("Perikanan Tangkap Umum", 11),
    PerikananTangkapLaut("Perikanan Tangkap Laut", 12),
    KehutananBudidaya("Kehutanan Budidaya", 13),
    KehutananTangkapUmum("Kehutanan Tangkap Umum", 14),
    KehutananPemungutanatauPenangkaran("Kehutanan Pemungutan atau Penangkaran", 15),
    JasaPertanian("Jasa Pertanian", 16)
}