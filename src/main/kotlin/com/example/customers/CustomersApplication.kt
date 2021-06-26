package com.example.customers

import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.annotation.Id
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux

@SpringBootApplication
class CustomersApplication

fun main(args: Array<String>) {
	runApplication<CustomersApplication>(*args)
}

@RestController
class CustomerRestController(private val repo: CustomerRepository) {

	@GetMapping("/customers")
	fun get() = this.repo.findAll()
}

@Component
class SampleDataInitializer(private val repo: CustomerRepository) : ApplicationRunner {
	override fun run(args: ApplicationArguments?) {

		// write data
		val names = Flux.just("Madhura", "Josh", "Spencer", "Stephane", "Olga",
			"Violetta", "Dr Syer", "Yuxin")
		val customers = names.map { Customer(null, it) }
		val saved = customers.flatMap { repo.save(it) }

		//fetch all
		val all = repo.findAll()
		saved.thenMany(all).subscribe {println(it)}
	}

}

interface CustomerRepository: ReactiveCrudRepository<Customer, Int>

data class Customer (@Id var id: Int?, val name: String)
