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
data class Review(
    val user: String,
    val comment: String,
    val rating: Int,
    val like: Boolean
)

@Serializable
data class Clothes(
    var id: Int,
    var picture: Picture,
    var name: String,
    var category: String,
    var likes: Int,
    var price: Double,
    var original_price: Double,
    var description: String,
    var reviews: MutableList<Review> = mutableListOf() // list of reviews
){
    val rating: Double
        get() = if (reviews.isNotEmpty()) reviews.map { it.rating }.average() else 0.0
}

// Starter JSON (full, from your example)
val clothesList = mutableListOf(
    Clothes(
        0,
        Picture(
            "https://raw.githubusercontent.com/OpenClassrooms-Student-Center/D-velopper-une-interface-accessible-en-Jetpack-Compose/main/img/accessories/1.jpg",
            "Orange handbag placed on a door handle"
        ),
        "Orange Handbag",
        "ACCESSORIES",
        56,
        69.99,
        69.99,
        description = "An elegant orange handbag hanging on a door handle, stylish design, compact yet spacious, perfect for daily outings or casual events.",
        reviews = mutableListOf(
            Review("Alice", "Great bag, very practical!", 5, true),
            Review("Bob", "Color is a bit flashy but nice.", 4, true)
        )
    ),
    Clothes(
        1,
        Picture(
            "https://raw.githubusercontent.com/OpenClassrooms-Student-Center/D-velopper-une-interface-accessible-en-Jetpack-Compose/main/img/bottoms/1.jpg",
            "Woman wearing jeans and yellow top"
        ),
        "Women’s Jeans",
        "BOTTOMS",
        55,
        49.99,
        59.99,
        description = "Women’s blue jeans paired with a yellow top, slim fit, casual yet fashionable, ideal for everyday wear or social outings.",
        reviews = mutableListOf(
            Review("Clara", "Very comfortable jeans.", 5, true),
            Review("David", "Size runs a bit small.", 3, true)
        )
    ),
    Clothes(
        2,
        Picture(
            "https://raw.githubusercontent.com/OpenClassrooms-Student-Center/D-velopper-une-interface-accessible-en-Jetpack-Compose/main/img/shoes/1.jpg",
            "Woman posing on the street wearing black rain boots"
        ),
        "Black Rain Boots",
        "SHOES",
        4,
        99.99,
        119.99,
        description = "Black rain boots worn by a woman on the street, sleek design, waterproof, comfortable for autumn walks and rainy days.",
        reviews = mutableListOf(
            Review("Eva", "Stylish and comfortable.", 4, true),
            Review("Frank", "A bit heavy for long walks.", 3, true)
        )
    ),
    Clothes(
        3,
        Picture(
            "https://raw.githubusercontent.com/OpenClassrooms-Student-Center/D-velopper-une-interface-accessible-en-Jetpack-Compose/main/img/tops/1.jpg",
            "Man in suit and blazer looking at the camera"
        ),
        "Brown Blazer",
        "TOPS",
        15,
        79.99,
        79.99,
        description = "Brown blazer on a man in a suit, formal style, tailored fit, ideal for office, meetings, or professional events.",
        reviews = mutableListOf(
            Review("George", "Elegant blazer, well tailored.", 5, true),
            Review("Hannah", "Fabric is a bit stiff.", 4, true)
        )
    ),
    Clothes(
        4,
        Picture(
            "https://raw.githubusercontent.com/OpenClassrooms-Student-Center/D-velopper-une-interface-accessible-en-Jetpack-Compose/main/img/tops/2.jpg",
            "Woman posing outside wearing green knitted sweater"
        ),
        "Green Sweater",
        "TOPS",
        15,
        29.99,
        39.99,
        description = "Green knitted sweater worn by a woman outside, warm and cozy, casual style, perfect for chilly autumn and winter days.",
        reviews = mutableListOf(
            Review("Isabel", "Soft and warm.", 4, true),
            Review("Jack", "Color matches the photo perfectly.", 4, true)
        )
    ),
    Clothes(
        5,
        Picture(
            "https://raw.githubusercontent.com/OpenClassrooms-Student-Center/D-velopper-une-interface-accessible-en-Jetpack-Compose/main/img/shoes/2.jpg",
            "Red high-heeled shoes on marble"
        ),
        "Red Heels",
        "SHOES",
        15,
        139.99,
        139.99,
        description = "Red high-heeled shoes placed on marble, elegant evening footwear, chic design, suitable for parties, formal events, or stylish occasions.",
        reviews = mutableListOf(
            Review("Karen", "Very chic but heels are a bit high.", 4, true),
            Review("Leo", "Perfect for an evening out!", 5, true)
        )
    ),
    Clothes(
        6,
        Picture(
            "https://raw.githubusercontent.com/OpenClassrooms-Student-Center/D-velopper-une-interface-accessible-en-Jetpack-Compose/main/img/accessories/2.jpg",
            "Worn adventurer backpack hanging from tree in forest"
        ),
        "Adventurer’s Backpack",
        "ACCESSORIES",
        9,
        69.99,
        99.99,
        description = "Worn-out adventurer’s backpack hanging from a tree in the forest, rugged, spacious, practical for hiking, camping, or outdoor adventures.",
        reviews = mutableListOf(
            Review("Mona", "Very practical for hiking.", 4, true),
            Review("Nate", "A bit small for my gear.", 3, true)
        )
    ),
    Clothes(
        7,
        Picture(
            "https://raw.githubusercontent.com/OpenClassrooms-Student-Center/D-velopper-une-interface-accessible-en-Jetpack-Compose/main/img/tops/3.jpg",
            "Young stylish man wearing jeans and bomber posing on street"
        ),
        "Autumn Bomber Jacket",
        "TOPS",
        30,
        89.99,
        109.99,
        description = "Stylish autumn bomber jacket on a young man, casual outfit, comfortable fit, suitable for street style, outdoor walks, and seasonal fashion.",
        reviews = mutableListOf(
            Review("Olivia", "Very trendy.", 4, true),
            Review("Paul", "Color a bit too dark.", 4, true)
        )
    ),
    Clothes(
        8,
        Picture(
            "https://raw.githubusercontent.com/OpenClassrooms-Student-Center/D-velopper-une-interface-accessible-en-Jetpack-Compose/main/img/tops/4.jpg",
            "Man in yellow sweatshirt looking right"
        ),
        "Yellow Sweatshirt",
        "TOPS",
        6,
        39.99,
        39.99,
        description = "Yellow sweatshirt on a man looking right, casual and comfortable, lightweight, ideal for spring, autumn, or everyday relaxed wear.",
        reviews = mutableListOf(
            Review("Quincy", "Comfortable and light.", 4, true),
            Review("Rachel", "A bit thin for winter.", 3, true)
        )
    ),
    Clothes(
        9,
        Picture(
            "https://raw.githubusercontent.com/OpenClassrooms-Student-Center/D-velopper-une-interface-accessible-en-Jetpack-Compose/main/img/tops/5.jpg",
            "Pink casual T-shirt hanging on a hanger"
        ),
        "Pink Casual T-Shirt",
        "TOPS",
        35,
        29.99,
        29.99,
        description = "Pink casual T-shirt hanging on a hanger, soft cotton fabric, perfect for summer days, relaxed style, and daily comfortable outfits.",
        reviews = mutableListOf(
            Review("Steve", "Soft and pleasant fabric.", 4, true),
            Review("Tina", "Perfect for summer.", 4, true)
        )
    ),
    Clothes(
        10,
        Picture(
            "https://raw.githubusercontent.com/OpenClassrooms-Student-Center/D-velopper-une-interface-accessible-en-Jetpack-Compose/main/img/accessories/3.jpg",
            "Round blue pendant in woman’s hand"
        ),
        "Blue Pendant",
        "ACCESSORIES",
        70,
        19.99,
        69.99,
        description = "Round blue pendant held in a woman’s hand, elegant accessory, delicate design, suitable for casual or formal occasions, adds charm.",
        reviews = mutableListOf(
            Review("Uma", "Very pretty pendant.", 5, true),
            Review("Victor", "A bit light.", 4, true)
        )
    ),
    Clothes(
        11,
        Picture(
            "https://raw.githubusercontent.com/OpenClassrooms-Student-Center/D-velopper-une-interface-accessible-en-Jetpack-Compose/main/img/bottoms/2.jpg",
            "Man in white shirt and black trousers sitting in forest"
        ),
        "Black Trousers",
        "BOTTOMS",
        54,
        49.99,
        69.99,
        description = "Black trousers worn by a man sitting in the forest, tailored fit, versatile fashion piece, perfect for formal or semi-casual wear.",
        reviews = mutableListOf(
            Review("Wendy", "Well tailored.", 4, true),
            Review("Xavier", "A bit expensive for the quality.", 3, true)
        )
    )
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