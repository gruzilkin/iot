package gruzilkin.iot.controllers

import gruzilkin.iot.entities.Device
import gruzilkin.iot.entities.Token
import gruzilkin.iot.repositories.DeviceRepository
import gruzilkin.iot.repositories.TokenRepository
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import java.security.Principal

@Controller
@RequestMapping("/devices")
class DevicesController(
    val devicesRepository: DeviceRepository,
    val tokenRepository: TokenRepository
) {
    @GetMapping
    fun index(principal: Principal, model: Model): String {
        model.addAttribute("name", principal.name)
        model.addAttribute("devices", devicesRepository.findAllByUserIdOrderById(principal.name.toLong()))
        return "devices/index"
    }

    @GetMapping("/{id}")
    fun getDevice(user: Principal, @PathVariable("id") id: Long, model: Model) : String {
        model.addAttribute("device", devicesRepository.findById(id).get())
        model.addAttribute("tokens", tokenRepository.findByDeviceId(id))
        return "devices/edit :: device-edit"
    }

    @PostMapping
    fun addDevice(user: Principal, name: String, model: Model) : String {
        devicesRepository.save(Device(userId = user.name.toLong(), name = name))
        model.addAttribute("devices", devicesRepository.findAllByUserIdOrderById(user.name.toLong()))
        return "devices/index :: device-list"
    }

    @DeleteMapping("/{id}")
    fun deleteDevice(user: Principal, @PathVariable("id") id: Long, model: Model) : String {
        devicesRepository.deleteById(id)
        model.addAttribute("devices", devicesRepository.findAllByUserIdOrderById(user.name.toLong()))
        return "devices/index :: device-list"
    }

    @PostMapping("/{id}/tokens")
    fun addToken(user: Principal, @PathVariable("id") deviceId: Long, model: Model) : String {
        tokenRepository.save(Token(deviceId))
        return "redirect:/devices/$deviceId"
    }

    @DeleteMapping("/{deviceId}/tokens/{tokenId}")
    fun deleteToken(user: Principal, @PathVariable("deviceId") deviceId: Long, @PathVariable("tokenId") tokenId: String, model: Model) : String {
        tokenRepository.deleteById(tokenId)
        return getDevice(user, deviceId, model)
    }
}