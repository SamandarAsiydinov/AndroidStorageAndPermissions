package uz.context.androidstorage

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import uz.context.androidstorage.databinding.ActivityMainBinding
import java.io.*
import java.nio.charset.Charset

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var isPersistent = true
    private var readText: String = ""

    var isInternal = true
    var readPermissionGranted = false
    var writePermissionGranted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()

    }

    private fun initViews() {
        requestPermissions()
        binding.btnSaveExternal.setOnClickListener {
            saveExternalFile("Android Developer")
        }
        binding.btnReadExternal.setOnClickListener {
            readExternalFile()
        }
    }

    private fun readExternalFile() {
        val fileName = "pdp_academy.txt"
        val file: File = if (isPersistent) {
            File(getExternalFilesDir(null), fileName)
        } else {
            File(externalCacheDir, fileName)
        }
    }

    private fun saveExternalFile(data: String) {
        val fileName = "pdp_academy.txt"
        val file: File = if (isPersistent) {
            File(getExternalFilesDir(null),fileName)
        } else {
            File(externalCacheDir,fileName)
        }
        try {
            val fileOutputStream = FileOutputStream(file)
            fileOutputStream.write(data.toByteArray(Charset.forName("UTF-8")))
            toast(String.format(("Write to %s successful"), fileName))
        } catch (e: Exception) {
            e.printStackTrace()
            toast(String.format(("Read from file %s failed $e"), fileName))
        }
    }

    private fun requestPermissions() {
        val hasReadPermission = ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        val hasWritePermission = ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        val minSdk29 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
        readPermissionGranted = hasReadPermission
        writePermissionGranted = hasWritePermission || minSdk29

        val permissionsToRequest = mutableListOf<String>()
        if (!readPermissionGranted)
            permissionsToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        if (!writePermissionGranted)
            permissionsToRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)

        if (permissionsToRequest.isNotEmpty())
            permissionLauncher.launch(permissionsToRequest.toTypedArray())
    }

    private val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        readPermissionGranted = permissions[Manifest.permission.READ_EXTERNAL_STORAGE] ?: readPermissionGranted
        writePermissionGranted = permissions[Manifest.permission.WRITE_EXTERNAL_STORAGE] ?: writePermissionGranted

        if (readPermissionGranted) toast("READ_EXTERNAL_STORAGE")
        if (writePermissionGranted) toast("WRITE_EXTERNAL_STORAGE")
    }

    private fun readInternalFile() {
        val fileName = "pdpInternal.txt"
        try {
            val fileInputStream: FileInputStream = if (isPersistent) {
                openFileInput(fileName)
            } else {
                val file = File(cacheDir, fileName)
                FileInputStream(file)
            }
            val inputStreamReader = InputStreamReader(fileInputStream, Charset.forName("UTF-8"))
            val lines: MutableList<String?> = ArrayList()
            val reader = BufferedReader(inputStreamReader)
            var line = reader.readLine()
            while (line != null) {
                lines.add(line)
                line = reader.readLine()
            }
            readText = TextUtils.join("\n", lines)
            toast(String.format(("Read from file %s successful"), fileName))
        } catch (e: Exception) {
            e.printStackTrace()
            toast(String.format(("Read from file %s failed $e"), fileName))
        }
    }

    private fun saveInternalFile(data: String) {
        val fileName = "pdpInternal.txt"
        try {
            val fileOutputStream: FileOutputStream = if (isPersistent) {
                openFileOutput(fileName, MODE_PRIVATE)
            } else {
                val file = File(cacheDir, fileName)
                FileOutputStream(file)
            }
            fileOutputStream.write(data.toByteArray(Charset.forName("UTF-8")))
            toast(String.format(("Write to %s successful"), fileName))
        } catch (e: Exception) {
            e.printStackTrace()
            toast(String.format(("Write to file %s failed"), fileName))
        }
    }

    private fun createInternalFile() {
        val fileName = "samandar_file.txt"
        val file: File = if (isPersistent) {
            File(filesDir, fileName)
        } else {
            File(cacheDir, fileName)
        }
        if (!file.exists()) {
            try {
                file.createNewFile()
                toast(String.format(("File %s has been created"), fileName))
            } catch (e: IOException) {
                toast(String.format(("File %s has been failed"), fileName))
            }
        } else {
            toast(String.format(("File %s already exists"), fileName))
        }
    }

    private fun checkStoragePaths() {
        val internalM1 = getDir("custom", 0)
        val internalM2 = filesDir

        val externalM1 = getExternalFilesDir(null)
        val externalM2 = externalCacheDir
        val externalM3 = getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        log(internalM1.absolutePath)
        log(internalM2.absolutePath)
        log(externalM1!!.absolutePath)
        log(externalM2!!.absolutePath)
        log(externalM3!!.absolutePath)

    }

    private fun toast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

    private fun log(msg: String) {
        Log.d("MainActivity@", msg)
    }
}