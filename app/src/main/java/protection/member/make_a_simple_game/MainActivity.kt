package protection.member.make_a_simple_game

import android.annotation.SuppressLint
import android.app.AlertDialog
import kotlinx.coroutines.Job

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.internal.Objects

import com.google.android.material.button.MaterialButton
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

import protection.member.make_a_simple_game.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var playerImage: ImageView
    private lateinit var computerImage: ImageView

    private lateinit var whoWinner: TextView

    private lateinit var rockButton: MaterialButton
    private lateinit var paperButton: MaterialButton
    private lateinit var scissorsButton: MaterialButton

    private lateinit var playerScore: TextView
    private lateinit var computerScore: TextView

    private var imageLoopJob: Job? = null

    private val arrayChoose = arrayOf(
        R.drawable.rock,
        R.drawable.paper,
        R.drawable.scissors
    )

    private var number = 0

    private var playerNumber = 0
    private var plyScore = 0

    private var computerNumber = 0
    private var comScore = 0

    private var winnerWrite = "Player and Computer"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val fireStore = FirebaseFirestore.getInstance()

        val user = hashMapOf(
            "FirstName" to "Chika",
            "LastName" to "Takami"
        )

        fireStore.collection("user").add(user).addOnSuccessListener {
            Toast.makeText(applicationContext, "Successfully", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(applicationContext, "Fail", Toast.LENGTH_SHORT).show()
        }

        playerImage = binding.player
        computerImage = binding.computer

        whoWinner = binding.winner
        whoWinner.text = winnerWrite

        rockButton = binding.rock
        paperButton = binding.paper
        scissorsButton = binding.scissors

        playerScore = binding.playerScore
        playerScore.text = "0"

        computerScore = binding.computerScore
        computerScore.text = "0"

        rockButton.setOnClickListener(this)
        scissorsButton.setOnClickListener(this)
        paperButton.setOnClickListener(this)
    }

    override fun onStart() {
        super.onStart()
        startImageLoop()
    }

    override fun onPause() {
        super.onPause()
        stopImageLoop()

    }

    private fun startImageLoop() {
        imageLoopJob = CoroutineScope(Dispatchers.Main).launch {
            rockButton.isEnabled = true
            paperButton.isEnabled = true
            scissorsButton.isEnabled = true

            while (isActive) {
                computerImage.setImageResource(arrayChoose[number])
                computerNumber = arrayChoose[number]
                number = (number + 1) % arrayChoose.size

                delay(10)
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.rock -> {
                playerImage.setImageResource(R.drawable.rock)
                playerNumber = R.drawable.rock
            }

            R.id.paper -> {
                playerImage.setImageResource(R.drawable.paper)
                playerNumber = R.drawable.paper
            }

            R.id.scissors -> {
                playerImage.setImageResource(R.drawable.scissors)
                playerNumber = R.drawable.scissors
            }
        }

        pauseLoopTemporarily()
    }

    private fun stopImageLoop() {
        whoWinner()

        imageLoopJob?.cancel()
        imageLoopJob = null
    }

    private fun pauseLoopTemporarily() {
        stopImageLoop()

        CoroutineScope(Dispatchers.Main).launch {
            rockButton.isEnabled = false
            paperButton.isEnabled = false
            scissorsButton.isEnabled = false
            delay(2000)
            startImageLoop()
        }
    }

    private fun whoWinner() {
        if (playerNumber == computerNumber) {
            winnerWrite = "Nobody is the winner"
        }

        else if ((playerNumber == R.drawable.rock && computerNumber == R.drawable.scissors) ||
            (playerNumber == R.drawable.scissors && computerNumber == R.drawable.paper) ||
            (playerNumber == R.drawable.paper && computerNumber == R.drawable.rock)) {

            plyScore += 1
            winnerWrite = "Player winner get 1 point"
        }

        else if ((computerNumber == R.drawable.rock && playerNumber == R.drawable.scissors) ||
            (computerNumber == R.drawable.scissors && playerNumber == R.drawable.paper) ||
            (computerNumber == R.drawable.paper && playerNumber == R.drawable.rock)) {

            comScore += 1
            winnerWrite = "Computer winner get 1 point"
        }

        theWinner()

        whoWinner.text = winnerWrite
        playerScore.text = "$plyScore"
        computerScore.text = "$comScore"
    }

    @SuppressLint("SetTextI18n")
    private fun theWinner() {
        if (plyScore == 3) {
            AlertDialog.Builder(this)
                .setTitle("The Player is Winner")
                .setMessage("The player is win, did you want to continue again?")
                .setPositiveButton("Restart") { dialog, which ->
                    plyScore = 0
                    comScore = 0
                    playerScore.text = "0"
                    computerScore.text = "0"
                    whoWinner.text = "Who is winner?"
                }
                .setNegativeButton("Exit") { dialog, which ->
                    finish()
                }
                .show()
        }

        else if (comScore == 3) {
            AlertDialog.Builder(this)
                .setTitle("The Computer is Winner")
                .setMessage("The Computer is win, did you want to continue again?")
                .setPositiveButton("Restart") { dialog, which ->
                    plyScore = 0
                    comScore = 0
                    playerScore.text = "0"
                    computerScore.text = "0"
                    whoWinner.text = "Who is winner?"
                }
                .setNegativeButton("Exit") { dialog, which ->
                    finish()
                }
                .show()
        }
    }
}
