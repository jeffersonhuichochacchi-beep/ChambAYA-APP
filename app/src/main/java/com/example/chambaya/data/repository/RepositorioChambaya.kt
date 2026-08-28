package com.example.chambaya.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.chambaya.data.model.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ChambayaRepository private constructor(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("chambaya_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    private var jobsList: MutableList<JobOffer> = mutableListOf()
    private var conversationsList: MutableList<ChatConversation> = mutableListOf()
    private var messagesMap: MutableMap<String, MutableList<ChatMessage>> = mutableMapOf()
    private var reviewsList: MutableList<Review> = mutableListOf()
    private var adsList: MutableList<BusinessAd> = mutableListOf()
    private var notificationsList: MutableList<AppNotification> = mutableListOf()

    var currentWorkerProfile: WorkerProfile
    var currentEmployerProfile: EmployerProfile
    var currentRole: String = ROLE_WORKER // ROLE_WORKER or ROLE_EMPLOYER

    companion object {
        const val ROLE_WORKER = "TRABAJADOR"
        const val ROLE_EMPLOYER = "CONTRATANTE"

        @Volatile
        private var INSTANCE: ChambayaRepository? = null

        fun getInstance(context: Context): ChambayaRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: ChambayaRepository(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    init {
        // Load or initialize default data
        currentWorkerProfile = loadWorkerProfile() ?: createDefaultWorkerProfile()
        currentEmployerProfile = loadEmployerProfile() ?: createDefaultEmployerProfile()
        currentRole = prefs.getString("current_role", ROLE_WORKER) ?: ROLE_WORKER

        loadJobs()
        loadConversations()
        loadReviews()
        loadAds()
        loadNotifications()
    }

    // --- ROLE MANAGEMENT ---
    fun switchRole(): String {
        currentRole = if (currentRole == ROLE_WORKER) ROLE_EMPLOYER else ROLE_WORKER
        prefs.edit().putString("current_role", currentRole).apply()
        return currentRole
    }

    // --- JOBS ---
    private fun loadJobs() {
        val json = prefs.getString("jobs_list", null)
        if (json.isNullOrEmpty()) {
            jobsList = createInitialJobs()
            saveJobs()
        } else {
            val type = object : TypeToken<MutableList<JobOffer>>() {}.type
            jobsList = gson.fromJson(json, type) ?: createInitialJobs()
        }
    }

    fun saveJobs() {
        prefs.edit().putString("jobs_list", gson.toJson(jobsList)).apply()
    }

    fun getJobs(query: String? = null, district: String? = null, category: String? = null): List<JobOffer> {
        return jobsList.filter { job ->
            val matchQuery = query.isNullOrBlank() ||
                    job.title.contains(query, ignoreCase = true) ||
                    job.description.contains(query, ignoreCase = true) ||
                    job.category.contains(query, ignoreCase = true)

            val matchDistrict = district.isNullOrBlank() || district == "Todos" || job.district.equals(district, ignoreCase = true)
            val matchCategory = category.isNullOrBlank() || category == "Todas" || job.category.equals(category, ignoreCase = true)

            matchQuery && matchDistrict && matchCategory
        }.sortedWith(compareByDescending<JobOffer> { it.isFeatured }.thenByDescending { it.createdAt })
    }

    fun getJobById(jobId: String): JobOffer? {
        return jobsList.find { it.id == jobId }
    }

    fun publishJob(
        title: String,
        category: String,
        district: String,
        address: String,
        payment: Double,
        paymentType: String,
        duration: String,
        schedule: String,
        workersNeeded: Int,
        date: String,
        description: String,
        isFeatured: Boolean
    ): JobOffer {
        val cost = if (isFeatured) 5.0 else 2.0
        currentWorkerProfile.walletBalance = maxOf(0.0, currentWorkerProfile.walletBalance - cost)
        saveWorkerProfile()

        val newJob = JobOffer(
            id = "JOB_${System.currentTimeMillis()}",
            title = title,
            category = category,
            district = district,
            address = address,
            payment = payment,
            paymentType = paymentType,
            duration = duration,
            schedule = schedule,
            workersNeeded = workersNeeded,
            date = date,
            description = description,
            isFeatured = isFeatured,
            employerId = currentEmployerProfile.id,
            employerName = currentEmployerProfile.fullName,
            employerRating = currentEmployerProfile.rating,
            employerPhone = currentEmployerProfile.phone,
            employerCompletedJobs = currentEmployerProfile.jobsPostedCount,
            status = "ABIERTA",
            applicantsCount = 0,
            isAppliedByMe = false,
            distanceKm = 0.5,
            latitude = getDistrictLatitude(district),
            longitude = getDistrictLongitude(district),
            createdAt = System.currentTimeMillis()
        )

        jobsList.add(0, newJob)
        currentEmployerProfile.jobsPostedCount += 1
        saveEmployerProfile()
        saveJobs()

        // Create alert notification
        addNotification(
            title = "Oferta publicada con éxito",
            message = "Tu chamba '${title}' ya está visible para los trabajadores de ${district}.",
            type = "SYSTEM"
        )

        return newJob
    }

    fun toggleApplyJob(jobId: String): Boolean {
        val job = jobsList.find { it.id == jobId } ?: return false
        job.isAppliedByMe = !job.isAppliedByMe
        if (job.isAppliedByMe) {
            job.applicantsCount += 1
            addNotification(
                title = "¡Postulación enviada!",
                message = "Has postulado a '${job.title}'. El contratante ${job.employerName} revisará tu perfil.",
                type = "JOB_ALERT"
            )
        } else {
            job.applicantsCount = maxOf(0, job.applicantsCount - 1)
        }
        saveJobs()
        return job.isAppliedByMe
    }

    // --- PROFILES ---
    private fun loadWorkerProfile(): WorkerProfile? {
        val json = prefs.getString("worker_profile", null) ?: return null
        return gson.fromJson(json, WorkerProfile::class.java)
    }

    fun saveWorkerProfile() {
        prefs.edit().putString("worker_profile", gson.toJson(currentWorkerProfile)).apply()
    }

    private fun loadEmployerProfile(): EmployerProfile? {
        val json = prefs.getString("employer_profile", null) ?: return null
        return gson.fromJson(json, EmployerProfile::class.java)
    }

    fun saveEmployerProfile() {
        prefs.edit().putString("employer_profile", gson.toJson(currentEmployerProfile)).apply()
    }

    // --- CHATS ---
    private fun loadConversations() {
        val json = prefs.getString("chat_conversations", null)
        if (json.isNullOrEmpty()) {
            conversationsList = createInitialConversations()
            saveConversations()
        } else {
            val type = object : TypeToken<MutableList<ChatConversation>>() {}.type
            conversationsList = gson.fromJson(json, type) ?: createInitialConversations()
        }
    }

    private fun saveConversations() {
        prefs.edit().putString("chat_conversations", gson.toJson(conversationsList)).apply()
    }

    fun getConversations(): List<ChatConversation> = conversationsList

    fun getOrCreateConversation(job: JobOffer): ChatConversation {
        val existing = conversationsList.find { it.jobId == job.id }
        if (existing != null) return existing

        val newConv = ChatConversation(
            id = "CONV_${System.currentTimeMillis()}",
            jobId = job.id,
            jobTitle = job.title,
            otherUserId = job.employerId,
            otherUserName = job.employerName,
            otherUserRole = "Contratante",
            lastMessage = "Hola, me interesa la oferta de ${job.title}",
            lastMessageTime = System.currentTimeMillis(),
            unreadCount = 0
        )
        conversationsList.add(0, newConv)
        saveConversations()

        // Init messages
        val initialMsgs = mutableListOf(
            ChatMessage(
                id = "MSG_1",
                conversationId = newConv.id,
                senderId = currentWorkerProfile.id,
                senderName = currentWorkerProfile.fullName,
                messageText = "Hola don ${job.employerName}, estoy disponible para el trabajo de ${job.title}.",
                isMine = true
            )
        )
        messagesMap[newConv.id] = initialMsgs
        return newConv
    }

    fun getMessages(conversationId: String): List<ChatMessage> {
        return messagesMap[conversationId] ?: createDefaultMessages(conversationId).also {
            messagesMap[conversationId] = it
        }
    }

    fun sendMessage(
        conversationId: String,
        text: String,
        isLocation: Boolean = false,
        locationAddress: String? = null,
        isProposal: Boolean = false,
        proposedPrice: Double? = null
    ): ChatMessage {
        val msg = ChatMessage(
            id = "MSG_${System.currentTimeMillis()}",
            conversationId = conversationId,
            senderId = currentWorkerProfile.id,
            senderName = currentWorkerProfile.fullName,
            messageText = text,
            isLocationShare = isLocation,
            locationAddress = locationAddress,
            isPriceProposal = isProposal,
            proposedPrice = proposedPrice,
            isMine = true
        )

        val list = messagesMap.getOrPut(conversationId) { mutableListOf() }
        list.add(msg)

        val conv = conversationsList.find { it.id == conversationId }
        conv?.let {
            it.lastMessage = text
            it.lastMessageTime = System.currentTimeMillis()
            saveConversations()
        }

        return msg
    }

    // --- REVIEWS ---
    private fun loadReviews() {
        val json = prefs.getString("reviews_list", null)
        if (json.isNullOrEmpty()) {
            reviewsList = createInitialReviews()
            saveReviews()
        } else {
            val type = object : TypeToken<MutableList<Review>>() {}.type
            reviewsList = gson.fromJson(json, type) ?: createInitialReviews()
        }
    }

    fun saveReviews() {
        prefs.edit().putString("reviews_list", gson.toJson(reviewsList)).apply()
    }

    fun getReviewsForUser(userId: String): List<Review> = reviewsList

    fun addReview(reviewerName: String, reviewerRole: String, rating: Float, comment: String, jobTitle: String): Review {
        val newReview = Review(
            id = "REV_${System.currentTimeMillis()}",
            targetUserId = currentWorkerProfile.id,
            reviewerName = reviewerName,
            reviewerRole = reviewerRole,
            rating = rating,
            comment = comment,
            jobTitle = jobTitle,
            date = "Hoy"
        )
        reviewsList.add(0, newReview)
        currentWorkerProfile.reviewsCount += 1
        currentWorkerProfile.rating = ((currentWorkerProfile.rating * (currentWorkerProfile.reviewsCount - 1)) + rating) / currentWorkerProfile.reviewsCount
        saveWorkerProfile()
        saveReviews()
        return newReview
    }

    // --- ADS & NOTIFICATIONS ---
    private fun loadAds() {
        adsList = createInitialAds()
    }

    fun getBusinessAds(): List<BusinessAd> = adsList

    private fun loadNotifications() {
        notificationsList = createInitialNotifications()
    }

    fun getNotifications(): List<AppNotification> = notificationsList

    fun addNotification(title: String, message: String, type: String) {
        val notif = AppNotification(
            id = "NOTIF_${System.currentTimeMillis()}",
            title = title,
            message = message,
            timeAgo = "Hace un momento",
            type = type,
            isRead = false
        )
        notificationsList.add(0, notif)
    }

    // --- INITIAL DATA GENERATORS (AYACUCHO CONTEXT) ---
    private fun createDefaultWorkerProfile(): WorkerProfile {
        return WorkerProfile(
            id = "USR_WORKER_101",
            fullName = "Leonel Cristiano Paitan",
            dni = "74892310",
            phone = "966 845 210",
            age = 27,
            district = "Carmen Alto",
            specialties = listOf("Pintura", "Albañilería", "Acabados"),
            experienceYears = 5,
            bio = "Especialista en pintura de interiores y exteriores, enlucido de yeso y trabajos rápidos de construcción en Huamanga.",
            rating = 4.9,
            reviewsCount = 18,
            completedJobsCount = 24,
            isDniVerified = true,
            isPhoneVerified = true,
            hourlyRateSuggested = 40.0,
            walletBalance = 35.0
        )
    }

    private fun createDefaultEmployerProfile(): EmployerProfile {
        return EmployerProfile(
            id = "USR_EMP_202",
            fullName = "Leonel Paitan (Contratante)",
            phone = "966 845 210",
            district = "Carmen Alto",
            rating = 4.8,
            jobsPostedCount = 4,
            isVerified = true
        )
    }

    private fun createInitialJobs(): MutableList<JobOffer> {
        return mutableListOf(
            JobOffer(
                id = "JOB_1",
                title = "2 Ayudantes para pintar fachada de 2 pisos",
                category = "Pintura",
                district = "Carmen Alto",
                address = "Av. Los Libertadores cdra 4 (Cerca al mirador)",
                payment = 100.0,
                paymentType = "por día",
                duration = "2 días",
                schedule = "7:30 AM - 4:30 PM",
                workersNeeded = 2,
                date = "29 de Agosto",
                description = "Se requiere personal con experiencia en pintura exterior con andamios. Se incluye almuerzo y refrigerio. Pago puntual al finalizar la jornada.",
                isFeatured = true,
                employerId = "EMP_1",
                employerName = "Carlos Mendoza",
                employerRating = 4.9,
                employerPhone = "966451230",
                employerCompletedJobs = 15,
                distanceKm = 0.8,
                latitude = -13.1720,
                longitude = -74.2210,
                createdAt = System.currentTimeMillis() - 1000 * 60 * 30
            ),
            JobOffer(
                id = "JOB_2",
                title = "Maestro Albañil para asentado de ladrillos",
                category = "Albañilería",
                district = "San Juan Bautista",
                address = "Jr. Mariano Bellido 320",
                payment = 140.0,
                paymentType = "por día",
                duration = "3 días",
                schedule = "8:00 AM - 5:00 PM",
                workersNeeded = 1,
                date = "30 de Agosto",
                description = "Levantamiento de muro perimétrico de 25m2 en segundo piso. Materiales y herramientas ya en obra. Se requiere seriedad y puntualidad.",
                isFeatured = true,
                employerId = "EMP_2",
                employerName = "Dra. Gladys Huamán",
                employerRating = 4.8,
                employerPhone = "966782341",
                employerCompletedJobs = 8,
                distanceKm = 1.4,
                latitude = -13.1700,
                longitude = -74.2290,
                createdAt = System.currentTimeMillis() - 1000 * 60 * 90
            ),
            JobOffer(
                id = "JOB_3",
                title = "Limpieza profunda de local comercial",
                category = "Limpieza",
                district = "Ayacucho Centro",
                address = "Jr. 28 de Julio (A 1 cdra de Plaza Mayor)",
                payment = 80.0,
                paymentType = "por tarea",
                duration = "5 horas",
                schedule = "2:00 PM - 7:00 PM",
                workersNeeded = 2,
                date = "Hoy mismo",
                description = "Limpieza de pisos cerámicos, vidrios y estantes antes de inauguración. Insumos de limpieza brindados en el local.",
                isFeatured = false,
                employerId = "EMP_3",
                employerName = "Boutique Andina",
                employerRating = 4.7,
                employerPhone = "966114477",
                employerCompletedJobs = 6,
                distanceKm = 0.4,
                latitude = -13.1606,
                longitude = -74.2259,
                createdAt = System.currentTimeMillis() - 1000 * 60 * 180
            ),
            JobOffer(
                id = "JOB_4",
                title = "Apoyo para mudanza y carga de muebles",
                category = "Mudanzas",
                district = "Jesús Nazareno",
                address = "Urb. Las Gardenias Mz B Lote 4",
                payment = 70.0,
                paymentType = "por tarea",
                duration = "Medio día (4 hrs)",
                schedule = "8:00 AM - 12:00 PM",
                workersNeeded = 2,
                date = "Sábado 30",
                description = "Cargar muebles de sala y cajas a camión de mudanza desde primer piso. Trabajo rápido con buen ambiente.",
                isFeatured = false,
                employerId = "EMP_4",
                employerName = "Marco Antonio Vega",
                employerRating = 5.0,
                employerPhone = "966998822",
                employerCompletedJobs = 4,
                distanceKm = 2.1,
                latitude = -13.1500,
                longitude = -74.2180,
                createdAt = System.currentTimeMillis() - 1000 * 60 * 300
            ),
            JobOffer(
                id = "JOB_5",
                title = "Gasfitero urgente para fuga en tubería de baño",
                category = "Gasfitería",
                district = "Ayacucho Centro",
                address = "Jr. Asamblea 180",
                payment = 90.0,
                paymentType = "por tarea",
                duration = "2 a 3 horas",
                schedule = "Inmediato",
                workersNeeded = 1,
                date = "Hoy",
                description = "Cambio de llave de paso y reparación de fuga bajo lavatorio. Se paga inmediato al probar que no hay fugas.",
                isFeatured = true,
                employerId = "EMP_5",
                employerName = "Hospedaje Colonial",
                employerRating = 4.9,
                employerPhone = "966332211",
                employerCompletedJobs = 19,
                distanceKm = 0.6,
                latitude = -13.1615,
                longitude = -74.2240,
                createdAt = System.currentTimeMillis() - 1000 * 60 * 420
            ),
            JobOffer(
                id = "JOB_6",
                title = "Ayudante de cocina para restaurante campestre",
                category = "Cocina",
                district = "Andrés Avelino Cáceres",
                address = "Vía de Evitamiento Km 2",
                payment = 75.0,
                paymentType = "por día",
                duration = "Domingo completo",
                schedule = "9:00 AM - 6:00 PM",
                workersNeeded = 2,
                date = "Domingo 31",
                description = "Picado de verduras, preparación de guarniciones y apoyo en despacho de platos típicos ayacuchanos (Puca picante, trucha frita). Incluye almuerzo.",
                isFeatured = false,
                employerId = "EMP_6",
                employerName = "Restaurante El Mirador",
                employerRating = 4.6,
                employerPhone = "966554433",
                employerCompletedJobs = 11,
                distanceKm = 3.0,
                latitude = -13.1480,
                longitude = -74.2120,
                createdAt = System.currentTimeMillis() - 1000 * 60 * 600
            )
        )
    }

    private fun createInitialConversations(): MutableList<ChatConversation> {
        return mutableListOf(
            ChatConversation(
                id = "CONV_1",
                jobId = "JOB_1",
                jobTitle = "Pintura fachada en Carmen Alto",
                otherUserId = "EMP_1",
                otherUserName = "Carlos Mendoza",
                otherUserRole = "Contratante",
                lastMessage = "Excelente Leonel, te espero mañana a las 7:30 AM en el punto acordado.",
                lastMessageTime = System.currentTimeMillis() - 1000 * 60 * 15,
                unreadCount = 1
            ),
            ChatConversation(
                id = "CONV_2",
                jobId = "JOB_2",
                jobTitle = "Albañilería en San Juan Bautista",
                otherUserId = "EMP_2",
                otherUserName = "Dra. Gladys Huamán",
                otherUserRole = "Contratante",
                lastMessage = "📍 Jr. Mariano Bellido 320, frente a la farmacia.",
                lastMessageTime = System.currentTimeMillis() - 1000 * 60 * 120,
                unreadCount = 0
            )
        )
    }

    private fun createDefaultMessages(convId: String): MutableList<ChatMessage> {
        return mutableListOf(
            ChatMessage(
                id = "M1",
                conversationId = convId,
                senderId = "EMP_1",
                senderName = "Carlos Mendoza",
                messageText = "Buenas tardes Leonel, vi tu perfil y tus calificaciones en pintura. ¿Tienes disponibilidad mañana?",
                isMine = false,
                timestamp = System.currentTimeMillis() - 1000 * 60 * 45
            ),
            ChatMessage(
                id = "M2",
                conversationId = convId,
                senderId = currentWorkerProfile.id,
                senderName = currentWorkerProfile.fullName,
                messageText = "Buenas tardes don Carlos, sí cuento con disponibilidad completa. Tengo mis propios rodillos y brochas de acabado.",
                isMine = true,
                timestamp = System.currentTimeMillis() - 1000 * 60 * 30
            ),
            ChatMessage(
                id = "M3",
                conversationId = convId,
                senderId = "EMP_1",
                senderName = "Carlos Mendoza",
                messageText = "Excelente Leonel, te espero mañana a las 7:30 AM en el punto acordado.",
                isMine = false,
                timestamp = System.currentTimeMillis() - 1000 * 60 * 15
            )
        )
    }

    private fun createInitialReviews(): MutableList<Review> {
        return mutableListOf(
            Review(
                id = "R1",
                targetUserId = currentWorkerProfile.id,
                reviewerName = "Ing. Roberto Alarcón",
                reviewerRole = "Contratante",
                rating = 5.0f,
                comment = "Excelente pintor, muy limpio en los acabados y sumamente puntual. Recomendado 100% en Ayacucho.",
                jobTitle = "Pintado de departamentos en Centro",
                date = "Hace 3 días"
            ),
            Review(
                id = "R2",
                targetUserId = currentWorkerProfile.id,
                reviewerName = "Sra. Carmen Sulca",
                reviewerRole = "Contratante",
                rating = 5.0f,
                comment = "Gran trabajo en la fachada de mi casa en San Juan Bautista. Buen precio y muy respetuoso.",
                jobTitle = "Pintura exterior y yeso",
                date = "Hace 1 semana"
            ),
            Review(
                id = "R3",
                targetUserId = currentWorkerProfile.id,
                reviewerName = "Ferretería Ayacucho SAC",
                reviewerRole = "Empresa",
                rating = 4.8f,
                comment = "Responsable y cumplió con todo el horario establecido sin retrasos.",
                jobTitle = "Ayudante en almacén",
                date = "Hace 2 semanas"
            )
        )
    }

    private fun createInitialAds(): MutableList<BusinessAd> {
        return mutableListOf(
            BusinessAd(
                id = "AD_1",
                name = "Ferretería El Maestro Ayacuchano",
                category = "Materiales & Herramientas",
                district = "San Juan Bautista",
                tagline = "Descuento en cemento, pinturas y alquiler de andamios para trabajadores de ChambAYA.",
                phone = "966123999",
                address = "Av. Cusco 450",
                promoBadge = "15% DSCTO"
            ),
            BusinessAd(
                id = "AD_2",
                name = "Botica & Farmacia San Cristóbal",
                category = "Salud & Botiquín",
                district = "Ayacucho Centro",
                tagline = "Botiquines para obras, analgésicos y atención rápida a 1 cuadra de la Plaza Mayor.",
                phone = "966456789",
                address = "Jr. 28 de Julio 120",
                promoBadge = "10% DSCTO"
            ),
            BusinessAd(
                id = "AD_3",
                name = "Maquinarias & Andamios Huamanga",
                category = "Alquiler de Equipos",
                district = "Carmen Alto",
                tagline = "Alquiler de trompitos, rotomartillos y escaleras telescópicas con entrega en obra.",
                phone = "966778899",
                address = "Av. Libertadores 890",
                promoBadge = "PROMO S/30"
            )
        )
    }

    private fun createInitialNotifications(): MutableList<AppNotification> {
        return mutableListOf(
            AppNotification(
                id = "N1",
                title = "🔥 Nueva chamba cerca de ti",
                message = "Se publicó '2 Ayudantes para pintar fachada' a 800m en Carmen Alto. ¡Postula antes que se agoten las vacantes!",
                timeAgo = "Hace 15 min",
                type = "JOB_ALERT",
                isRead = false
            ),
            AppNotification(
                id = "N2",
                title = "✅ DNI Verificado con éxito",
                message = "Tu perfil cuenta ahora con la insignia oficial de confianza ChambAYA.",
                timeAgo = "Hace 2 horas",
                type = "SYSTEM",
                isRead = true
            ),
            AppNotification(
                id = "N3",
                title = "⭐ Nueva calificación de 5 estrellas",
                message = "El contratante Ing. Roberto Alarcón ha dejado una reseña excelente sobre tu trabajo.",
                timeAgo = "Ayer",
                type = "RATING",
                isRead = true
            )
        )
    }

    private fun getDistrictLatitude(district: String): Double {
        return when (district) {
            "Carmen Alto" -> -13.1720
            "San Juan Bautista" -> -13.1700
            "Jesús Nazareno" -> -13.1500
            "Andrés Avelino Cáceres" -> -13.1480
            else -> -13.1606 // Ayacucho Centro
        }
    }

    private fun getDistrictLongitude(district: String): Double {
        return when (district) {
            "Carmen Alto" -> -74.2210
            "San Juan Bautista" -> -74.2290
            "Jesús Nazareno" -> -74.2180
            "Andrés Avelino Cáceres" -> -74.2120
            else -> -74.2259 // Ayacucho Centro
        }
    }
}
