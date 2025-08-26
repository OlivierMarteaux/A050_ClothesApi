import io.ktor.server.engine.*
import io.ktor.server.cio.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.ktor.server.plugins.contentnegotiation.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import io.ktor.serialization.kotlinx.json.*
import java.util.concurrent.atomic.AtomicInteger

// Data classes
@Serializable
data class Picture(val url: String, val description: String)

@Serializable
data class Clothes(
    var id: Int,
    var picture: Picture,
    var name: String,
    var category: String,
    var likes: Int,
    var price: Double,
    var original_price: Double
)

// Starter JSON (full, from your example)
val clothesList = mutableListOf(
    Clothes(0, Picture("https://raw.githubusercontent.com/OpenClassrooms-Student-Center/D-velopper-une-interface-accessible-en-Jetpack-Compose/main/img/accessories/1.jpg", "Sac à main orange posé sur une poignée de porte"), "Sac à main orange", "ACCESSORIES", 56, 69.99, 69.99),
    Clothes(1, Picture("https://raw.githubusercontent.com/OpenClassrooms-Student-Center/D-velopper-une-interface-accessible-en-Jetpack-Compose/main/img/bottoms/1.jpg", "Modèle femme qui porte un jean et un haut jaune"), "Jean pour femme", "BOTTOMS", 55, 49.99, 59.99),
    Clothes(2, Picture("https://raw.githubusercontent.com/OpenClassrooms-Student-Center/D-velopper-une-interface-accessible-en-Jetpack-Compose/main/img/shoes/1.jpg", "Modèle femme qui pose dans la rue en bottes de pluie noires"), "Bottes noires pour l'automne", "SHOES", 4, 99.99, 119.99),
    Clothes(3, Picture("https://raw.githubusercontent.com/OpenClassrooms-Student-Center/D-velopper-une-interface-accessible-en-Jetpack-Compose/main/img/tops/1.jpg", "Homme en costume et veste de blazer qui regarde la caméra"), "Blazer marron", "TOPS", 15, 79.99, 79.99),
    Clothes(4, Picture("https://raw.githubusercontent.com/OpenClassrooms-Student-Center/D-velopper-une-interface-accessible-en-Jetpack-Compose/main/img/tops/2.jpg", "Femme dehors qui pose avec un pull en maille vert"), "Pull vert femme", "TOPS", 15, 29.99, 39.99),
    Clothes(5, Picture("https://raw.githubusercontent.com/OpenClassrooms-Student-Center/D-velopper-une-interface-accessible-en-Jetpack-Compose/main/img/shoes/2.jpg", "Escarpins rouges posés sur du marbre"), "Escarpins de soirée", "SHOES", 15, 139.99, 139.99),
    Clothes(6, Picture("https://raw.githubusercontent.com/OpenClassrooms-Student-Center/D-velopper-une-interface-accessible-en-Jetpack-Compose/main/img/accessories/2.jpg", "Sac d'aventurier usé accroché dans un arbre en forêt"), "Sac à dos d'aventurier", "ACCESSORIES", 9, 69.99, 99.99),
    Clothes(7, Picture("https://raw.githubusercontent.com/OpenClassrooms-Student-Center/D-velopper-une-interface-accessible-en-Jetpack-Compose/main/img/tops/3.jpg", "Homme jeune stylé en jean et bomber qui pose dans la rue"), "Bomber automnal pour homme", "TOPS", 30, 89.99, 109.99),
    Clothes(8, Picture("https://raw.githubusercontent.com/OpenClassrooms-Student-Center/D-velopper-une-interface-accessible-en-Jetpack-Compose/main/img/tops/4.jpg", "Homme en sweat jaune qui regarde à droite"), "Sweat jaune", "TOPS", 6, 39.99, 39.99),
    Clothes(9, Picture("https://raw.githubusercontent.com/OpenClassrooms-Student-Center/D-velopper-une-interface-accessible-en-Jetpack-Compose/main/img/tops/5.jpg", "T-shirt rose posé sur un cintre dans une penderie"), "T-shirt casual rose", "TOPS", 35, 29.99, 29.99),
    Clothes(10, Picture("https://raw.githubusercontent.com/OpenClassrooms-Student-Center/D-velopper-une-interface-accessible-en-Jetpack-Compose/main/img/accessories/3.jpg", "Pendentif rond bleu dans la main d'une femme"), "Pendentif bleu pour femme", "ACCESSORIES", 70, 19.99, 69.99),
    Clothes(11, Picture("https://raw.githubusercontent.com/OpenClassrooms-Student-Center/D-velopper-une-interface-accessible-en-Jetpack-Compose/main/img/bottoms/2.jpg", "Homme en chemise blanche et pantalon noir assis dans la forêt"), "Pantalon noir", "BOTTOMS", 54, 49.99, 69.99)
)

val nextId = AtomicInteger(clothesList.size)

fun main() {
    embeddedServer(CIO, port = 8080, host = "0.0.0.0") {
        install(ContentNegotiation) {
            json()
        }

        routing {
            get("/clothes") {
                call.respond(clothesList)
            }

            get("/clothes/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                val item = clothesList.find { it.id == id }
                if (item != null) call.respond(item)
                else call.respond(mapOf("error" to "Clothes not found"))
            }

            post("/clothes") {
                val newClothes = call.receive<Clothes>()
                newClothes.id = nextId.getAndIncrement()
                clothesList.add(newClothes)
                call.respond(newClothes)
            }

            put("/clothes/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                val index = clothesList.indexOfFirst { it.id == id }
                if (index != -1) {
                    val updated = call.receive<Clothes>()
                    updated.id = id!! // preserve ID
                    clothesList[index] = updated
                    call.respond(updated)
                } else {
                    call.respond(mapOf("error" to "Clothes not found"))
                }
            }
        }
    }.start(wait = true)
}