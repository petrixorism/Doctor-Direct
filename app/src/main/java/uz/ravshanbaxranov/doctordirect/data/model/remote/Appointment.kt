package uz.ravshanbaxranov.doctordirect.data.model.remote

data class Appointment(
    var id: String = "",
    val patient: String = "",
    val doctor: String = "",
    val speciality: String = "",
    val diagnosis: String = "",
    val aim: String = "",
    val recipe: String = "",
    val conclusion: String = "",
    var patientUsername: String = "",
    val doctorUsername: String = "",
    val status: String = "Upcoming",
    val arrivalDate: String = "",
    val date: Long = System.currentTimeMillis()
)
