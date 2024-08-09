package gruzilkin.iot.controllers

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/debug")
class DebugController {
    @GetMapping("/is-virtual-thread")
    fun checkThread(): String {
        return "Is Virtual Thread: " + Thread.currentThread().isVirtual
    }
}