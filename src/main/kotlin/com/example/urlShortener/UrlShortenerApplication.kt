package com.example.urlShortener

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.annotation.Id
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.view.RedirectView
import java.math.BigInteger
import java.security.MessageDigest


fun md5(input:String): String {
	val md = MessageDigest.getInstance("MD5")
	return BigInteger(1, md.digest(input.toByteArray())).toString(16).padStart(32, '0')
}

@SpringBootApplication
class UrlShortenerApplication

fun main(args: Array<String>) {
	runApplication<UrlShortenerApplication>(*args)
}

@Table("urls")
data class Url(
		@Id val id: String?,
		val shorten_url: String?,
		val url: String
)

interface UrlRepository : CrudRepository<Url, String>{
	@Query("select * from urls")
	fun findUrls(): List<Url>

	@Query("select * from urls where url = :url")
	fun findUrl(url: String): Url?

	@Query("select url from urls where shorten_url = :shorten_url")
	fun findOriginalUrl(@Param("shorten_url") shorten_url: String): String?
}

@Service
class UrlService(val db: UrlRepository) {

	fun findUrls(): List<Url> = db.findUrls()

	fun findUrl(url: String): Url? = db.findUrl(url)

	fun findOriginalUrl(shorten_url: String): String? = db.findOriginalUrl(shorten_url)

	@Transactional
	fun create(request: Url): Url {
		val existingUrl = findUrl(request.url)
		if(existingUrl != null) {
			return existingUrl
		}
		val newUrl = Url(
				id = null,
				shorten_url = md5(request.url).take(9),
				url = request.url
		)
		db.save(newUrl)
		return newUrl
	}

}

@RestController
class UrlResource(val service: UrlService) {
	@GetMapping
	fun index(): List<Url> = service.findUrls()

	@PostMapping
	fun create(@RequestBody request: Url): Url {
		return service.create(request)
	}

	@GetMapping(value = ["/{shorten_url}"])
	fun redirect(@PathVariable("shorten_url") shorten_url: String): RedirectView? {
		val redirectView = RedirectView()
		val url = service.findOriginalUrl(shorten_url)
		if(url == null){
			println("client request for shorten URL $shorten_url doesn't exist.")
			redirectView.url = ""
			redirectView.setStatusCode(HttpStatus.NO_CONTENT)
		} else {
			println("Redirect client to $url")
			redirectView.url = url
			redirectView.setStatusCode(HttpStatus.NOT_MODIFIED)
		}
		return redirectView
	}
}

