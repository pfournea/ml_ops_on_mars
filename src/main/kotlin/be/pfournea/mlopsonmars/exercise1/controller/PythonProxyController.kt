package be.pfournea.mlopsonmars.exercise1.controller

import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.util.ResourceUtils
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader


@RestController
class PythonProxyController {

    @PostMapping("/predict", consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun predict(@RequestBody data: Data): ResponseEntity<String> {
        return ResponseEntity.ok(executeScript(data.data))
    }

    fun executeScript(data: List<Int>): String {
        val file: File = ResourceUtils.getFile("classpath:model1.py")
        val cmd = "echo \"[${data.joinToString(",")}]\" | python ${file.absolutePath}"
        val process = Runtime.getRuntime().exec(arrayOf("/bin/sh", "-c", cmd))
        process.waitFor()
        val reader = BufferedReader(InputStreamReader(process.inputStream))
        val resultLine = reader.lines().filter { line -> line.startsWith("result:") }.findFirst().orElse("result: {\"no result found\"}")
        return resultLine.substringAfter("result: ")
    }
}

data class Data(val data: List<Int>)