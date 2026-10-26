package com.levipack.freecam

import android.os.SystemClock
import kotlin.math.*

/**
 * LeviPack Freecam Engine for Android Minecraft Bedrock (v1.26.52)
 * Primary Asset Bundle Tag: LeviAssetBundle
 * Frame Rate Target: 60 FPS (Hardware VSync Synchronized)
 */
class FreecamController(
    private val config: FreecamConfig = FreecamConfig.default()
) {
    // Current Camera Transform
    var posX = 0.0; var posY = 64.0; var posZ = 0.0
    var yaw = 0.0f; var pitch = 0.0f
    var currentFov = 70.0f
    var targetFov = 70.0f

    // Detached Player Reference (Preserves original position)
    var isFreecamActive = false
        private set
    var savedPlayerX = 0.0; var savedPlayerY = 0.0; var savedPlayerZ = 0.0
    var savedPlayerYaw = 0.0f; var savedPlayerPitch = 0.0f

    // Inertial velocity vectors for smooth interpolation
    private var velocityX = 0.0
    private var velocityY = 0.0
    private var velocityZ = 0.0
    private var rotVelocityYaw = 0.0f
    private var rotVelocityPitch = 0.0f

    // 60 FPS Hardware Frame Synchronizer
    private val frameSync = FrameSyncChoreographer(targetFps = 60) { alpha, dt ->
        onVsyncRenderTick(alpha, dt)
    }

    // Asset Bundle Management
    private val assetManager = LeviAssetBundle.getInstance()

    fun enableFreecam(initialX: Double, initialY: Double, initialZ: Double, initialYaw: Float, initialPitch: Float) {
        if (isFreecamActive) return
        isFreecamActive = true
        savedPlayerX = initialX
        savedPlayerY = initialY
        savedPlayerZ = initialZ
        savedPlayerYaw = initialYaw
        savedPlayerPitch = initialPitch

        posX = initialX
        posY = initialY + 1.62 // Eye level
        posZ = initialZ
        yaw = initialYaw
        pitch = initialPitch
        currentFov = config.defaultFov
        targetFov = config.defaultFov

        // Reset velocities
        velocityX = 0.0; velocityY = 0.0; velocityZ = 0.0

        // Lock onto Choreographer 60 FPS hardware loop
        frameSync.start()
        NativeFreecamBridge.nativeOnFreecamStateChanged(true)
    }

    fun disableFreecam() {
        if (!isFreecamActive) return
        isFreecamActive = false
        frameSync.stop()
        NativeFreecamBridge.nativeOnFreecamStateChanged(false)
    }

    /**
     * Input injection from Bedrock touch / gamepad / keyboard
     */
    fun processMoveInput(forward: Float, strafe: Float, upDown: Float, isFast: Boolean, isSlow: Boolean, dt: Float) {
        if (!isFreecamActive) return

        val speed = when {
            isFast -> config.moveSpeed * config.fastSpeedMultiplier
            isSlow -> config.moveSpeed * config.slowSpeedMultiplier
            else -> config.moveSpeed
        }

        val yawRad = Math.toRadians(yaw.toDouble())
        val pitchRad = Math.toRadians(pitch.toDouble())

        // Calculate 3D spherical directional vector
        val cosYaw = cos(yawRad); val sinYaw = sin(yawRad)
        val cosPitch = cos(pitchRad); val sinPitch = sin(pitchRad)

        // Direction vectors
        val fwdX = -sinYaw * cosPitch
        val fwdY = -sinPitch
        val fwdZ = cosYaw * cosPitch

        val strafeX = cosYaw
        val strafeZ = sinYaw

        val targetVx = (fwdX * forward + strafeX * strafe) * speed
        val targetVy = (fwdY * forward + upDown) * speed
        val targetVz = (fwdZ * forward + strafeZ * strafe) * speed

        // Smooth Movement Interpolation (inertial)
        val smoothing = config.smoothingFactor.toDouble()
        velocityX = velocityX * smoothing + targetVx * (1.0 - smoothing)
        velocityY = velocityY * smoothing + targetVy * (1.0 - smoothing)
        velocityZ = velocityZ * smoothing + targetVz * (1.0 - smoothing)

        // Dynamic FOV speed scaling
        if (config.dynamicFovSpeedScaling) {
            val speedNorm = sqrt(velocityX * velocityX + velocityY * velocityY + velocityZ * velocityZ)
            val fovBoost = ((speedNorm / config.moveSpeed) * 6.0f).coerceIn(0.0, 14.0).toFloat()
            targetFov = config.defaultFov + fovBoost
        }
    }

    fun setZoom(zoomed: Boolean) {
        targetFov = if (zoomed) config.zoomFov else config.defaultFov
    }

    /**
     * Called at 60 FPS hardware VSync tick with sub-frame alpha interpolation
     */
    private fun onVsyncRenderTick(alpha: Float, dt: Float) {
        // Integrate position
        posX += velocityX * dt
        posY += velocityY * dt
        posZ += velocityZ * dt

        // Smooth FOV transition
        if (abs(currentFov - targetFov) > 0.05f) {
            val fovStep = (targetFov - currentFov) * (dt * 10.0f).coerceAtMost(1.0f)
            currentFov += fovStep
        }

        // Upload to Bedrock Camera Matrix via C++ NDK
        NativeFreecamBridge.nativeUpdateCameraMatrix(
            posX, posY, posZ,
            yaw, pitch,
            currentFov,
            alpha
        )
    }
}
