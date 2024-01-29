package com.example.didaktikapp

import android.graphics.PorterDuff
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ProgressBar
import com.example.didaktikapp.navigation.NavigationUtil
import com.example.didaktikapp.titleFragment.TitleFragment

class Info : AppCompatActivity() {

    val informacion = hashMapOf<String, Informacion>(
        "Juego1.2" to Informacion(
            "Une honetan, Lezamako nekazaritza-kooperatibaren ondoan gaude. Bertan, urtez urte herriko idi-probak egiten dira. Lezamako kasuan, proba hauek urtero egiten dira jaietan. Normalean, idiek 500 eta 650 kg arteko pisua izaten dute. Proba edo apustu garrantzitsuen aurreko egunetan, idiei lana murriztu eta elikadura-erregimen berezi bat ezartzen zaie; erregimen horren oinarria babak dira.",
            R.raw.juegounodos
        ),
        "Juego2.2" to Informacion(
            "Bizkaiko odolosterik onenak Lezaman daude, sariren bat lortuta dute jada. Odolosteak txarri odolez, kipulaz, porruz, hesteekaz, koipeagaz, arrozaz, piperrautsaz eta gaztaz egiten dira. Gainera, herriko jaien bezperan, odolosteen afaria egiten da ohitura moduan. Lezamako jaietan izaten dira egiten diren lehenak eta maiatza inguruan odolosteak egiteari uzten diote.",
            R.raw.juegodosuno
        ),
        "Juego2.4" to Informacion(
            "(falta el texto)",
            R.raw.juegodoscuatro
        ),
        "Juego3.2" to Informacion(
            "Argazkian ikusten den edaria txakolina da. Txakolina ardo zuria da, Courbu barietateko mahatsetik abiatuta egiten dena. Txakolina, batez ere, Euskal Herrian ekoizten da.",
            R.raw.juegotresdos
        ),
        "Juego3.3" to Informacion(
            "Orain Espainiako upategi ospetsuenetariko batean gaude, “Lezamako Magalarte Txakolina”. Upategi hau familiako negozio bat da eta mahastizaintza eta ardogintza tradizio handia du. Txakolina eratzen dute teknika tradizionaletan oinarrituz eta gaur egungo teknologien laguntzarekin.\n",
            R.raw.juegotrestres
        ),
        "Juego3.5" to Informacion(
            "(falta el texto)",
            R.raw.juegotrescinco
        ),
        "Juego4.1" to Informacion(
            "Mikel Zarate Lejarraga Lezaman jaio zan 1933an. Idazlea zan, euskal idazlea konkretuki. 1970eko hamarkadaren hasieratik Derioko Udako Ikastaroetan lan egin zuen irakasle bezala eta Bilboko Hizkuntza Eskola Ofizialean ere bai. 1976an La Gran Enciclopedia Vasca argitaletxeak bere doktore tesia argitaratu zuen: Influencias del vascuence en la lengua castellana. Literatura lanen arloan, Lezamakoak, prosaz nahiz bertsoz idatzi zuen. Eleberri bakarra argitaratu zuen: Haurgintza minetan (1973) eta beste bat amaitu gabe utzi zuen. \n",
            R.raw.juegocuatrouno
        ),
        "Juego4.4" to Informacion(
            "(falta el texto)",
            R.raw.juegocuatrocuatro
        ),
        "Juego5.1" to Informacion(
            "Oraintxe Santa Maria Elizan gaude, Lezamako herriaren erdian kokatuta dagoena. Santa Maria Eliza XVI. mendean eraiki zen eta estilo gotikoa dauka",
            R.raw.juegocincouno
        ),
        "Juego5.3" to Informacion(
            "Badakizuenez elizako kanpaiak orduak adierazten ditu, goizeko 8retatik gaueko 9etararte. Orduro jotzen da eta kolpeak orduen arabera dira baita, hau da, 8rak badira 8 kolpe entzungo dira, 9etan 9 kolpe… horrela eguerdiko 12ak arte. Ordu horretatik aurrera berriz hasten da zerotik, hau da, ordu batetan kolpe bat entzungo da eta ez 13. 12 orduko sistemarekin funtzionatzen du. \n" +
                    "Kanpaiak norbait hil dela abisatzeko jotzen dira ere bai. Hildakoa gizonezkoa bada, 3 kanpaia jotzen dira, beste kolpe fase batek segitzen dio eta bukatzeko 3 kanpaia berriro ere. Hildakoa emakumea bada, lehenengo 2 kanpaia, segidan fase bat eta bukatzeko 3 baita ere.\n",
            R.raw.juegocincotres
        ),
        "Juego5.6" to Informacion(
            "(falta el texto)",
            R.raw.juegocincoseis
        ),
        "Juego6.1" to Informacion(
            "\"San Mamés\" futbol zelaia 1913an eraiki zen eta geroztik, hainbat aldiz handitu egin da. 1945ean zelaia handitu zuten eta horrexegatik, mendebaldeko tribunaren estalkian altzairuzko arku handi bat sartu zuten. Honako hau, futbol-zelaiaren ezaugarrietako bat bihurtu zen eta baita klub berarena ere.\n" +
                    "San Mames futbol-zelaia 2013an eraitsi zuten eta herri-mobilizazio batzuen ondoren, arkua indultatzea erabaki zuten. Hortaz, arkua desmuntatu, zaharberritu eta Athleticek Lezaman dituen instalazioetara eraman zuten.\n",
            R.raw.juegoseisuno
        ),
        "Juego6.3" to Informacion(
            "(falta el texto)",
            R.raw.juegoseistres
        ),
        "Juego7.1" to Informacion(
            "Aretxalde auzora ailegatu gara eta Lezama dorrea dugu gure aurrean. (Lezamako dorrearen argazkia)\n" +
                    "Dorre hau gotorleku militarra izan zen. Gainera, botere ekonomiko eta sozialaren sinboloa izan zen, dorreak jabeen boterezko jatorria adierazten zuelako.\n" +
                    "Dorrea XVI. mendean eraiki zen. Hareharriz eginda dago eta hiru solairu ditu egurrezko eskailera baten bidez lotutakoak. Baina zer dago horren barruan? Aintzinean, lehenengo solairua abereentzat zen, bigarrena, bizitzeko solairua zen eta azkenengoan, areto handi bat zegoen non argia baoetatik sartzen zen.\n" +
                    "Gaur egun, dorrea etxebizitza bihurtu da eta horren ondorioz, aldaketak egin dira barruan zein kanpoan.\n",
            R.raw.juegosieteuno
        ),
        "HASIERAKO JARDUERA.1" to Informacion(
            "Nor zarete zuek? Ez zaituztet inoiz hemendik ikusi… Kaixo, ni Lamia naiz, eta aspalditik bizi izan naiz Lezamako inguruetan beste lamiekin batera, batez ere ibaietan. Beti egon naiz hemen, beraz, Lezama eta horren historia guztia ondo baino hobeto ezagutzen dut.\n" +
                    "Itxaron momentu bat, ah zelako ideia izan dudan! Lezamako ondarea ezagutzera etorri zarete ezta? (isilune laburra) Ba nik izango naiz zuen gidaria abentura harrigarri honetan. Herri honen leku eta historia esanguratsuena zuekin partekatzeko irrikaz nago.\n" +
                    "Bidaia honetan, Lezamako ondare kultural eta historikoaren parte diren toki garrantzitsuenetako batzuk bisitatuko ditugu, hain zuzen ere, Lezamako nekazaritza-kooperatiba, herriko harategia, “Lezamako Magalarte Txakolina” upategia, herriko udaletxea, Santa Maria Eliza, Athletic Clubeko instalazioak eta Lezamako Dorrea. Gainera, leku horietan egiteko hainbat jarduera pentsatu ditut. \n" +
                    "Prest zaudete?. Bada, goazen Lezama ezagutzera!\n",
            R.raw.hjuno
        ),
        "AMAIERAKO JARDUERA.1" to Informacion(
            "Gure ibilbidearen amaierara heldu gara. Lezamako herritik hainbat buelta eman ditugu leku esanguratsu zein pertsona garrantzitsuei buruzko informazioa berenaganatuz eta tokiko ohiturak ezagutuz. Harrotasun osoz, Lezamako ondarea guztiz ezagutu duzuela esan dezaket eta horren berri izateari esker, biharamunean bere ondarea bizi mantentzen lagunduko duzue baita, nik egiten dudan moduan.\n" +
                    "Segurazki neke-neke egongo zarete, baina Lezamatik alde egin baino lehen, azken lantxo bat daukat zuentzat. Argi daukat adi egon zaretela ibilbide osoan zehar, beraz, erraza izango da. Mapa bat bete beharko duzue egin duzuen ibilbidea irudikatuz, horrela bisitatutako puntu garrantzitsu horiek beti gogoratuko dituzue\n",
            R.raw.ajuno
        ),
        "AMAIERAKO JARDUERA.3" to Informacion(
            "Bidaiari agur esateko momentua ailegatu da. Lezamako herria goitik behera bisitatzearekin batera izugarri ikasi duzue. Bertako ohiturak, pertsonak eta lekuak ezagutu dituzue eta horren bizipena betirako izango duzue. Orain Lezama zuen bihotzen parte da.\n" +
                    "Gidari ona izan duzue baina zuek, duda barik, ikasle bikainak izan zarete.\n" +
                    "Eskerrik asko eta laster arte neska-mutilak!\n",
            R.raw.ajtres
        ),
    )

    var mediaPlayer: MediaPlayer? = null
    private var gameManagerService: GameManagerService? = GameManagerService()
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)

        intent.getDoubleExtra("punto", 0.0)

        gameManagerService = GameManager.get()
        progressBar = findViewById(R.id.ordenarImagenesProgressBar)
        gameManagerService!!.setInitialProgress(progressBar)

        val pantalla = gameManagerService?.pantallaActual()

        findViewById<TextView>(R.id.info).text = informacion[pantalla]?.texto

        setupHeaderFragment(savedInstanceState)

        //Audios
        mediaPlayer = MediaPlayer.create(this, informacion[pantalla]?.audio!!)
        mediaPlayer?.start()

        val playaudio = findViewById<ImageButton>(R.id.playaudio)
        val pauseaudio = findViewById<ImageButton>(R.id.pauseaudio)

        playaudio.setOnClickListener{
            mediaPlayer?.start()
            playaudio.setColorFilter(resources.getColor(R.color.verdeOscuro), PorterDuff.Mode.SRC_IN)
            pauseaudio.setColorFilter(resources.getColor(R.color.grisOscuro), PorterDuff.Mode.SRC_IN)
        }
        pauseaudio.setOnClickListener{
            mediaPlayer?.pause()

            pauseaudio.setColorFilter(resources.getColor(R.color.verdeOscuro), PorterDuff.Mode.SRC_IN)
            playaudio.setColorFilter(resources.getColor(R.color.grisOscuro), PorterDuff.Mode.SRC_IN)
        }

        //Boton terminar
        findViewById<Button>(R.id.terminar_info).setOnClickListener{
            gameManagerService?.addProgress(progressBar)
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null

            GameManager.get()?.nextScreen()
        }
    }

    private fun setupHeaderFragment(savedInstanceState: Bundle?) {
        val fragmentContainer = findViewById<FrameLayout>(R.id.titleFragmentTag)
        if (savedInstanceState == null) {
            val titleFragment = TitleFragment.newInstance(resources.getString(R.string.infoTitle))
            supportFragmentManager.beginTransaction()
                .replace(fragmentContainer.id, titleFragment, "titleFragmentTag")
                .commit()
        }
        val titleFragment =
            supportFragmentManager.findFragmentByTag("titleFragmentTag") as TitleFragment?
        titleFragment?.setOnHomeButtonClickListener {
            onHomeButtonClicked()
        }
    }

    private fun onHomeButtonClicked() {
        NavigationUtil.navigateToMainMenu(this)
    }
    override fun onDestroy() {
        // Liberar los recursos del MediaPlayer cuando la actividad se destruye
        mediaPlayer?.release()
        super.onDestroy()
    }

    data class Informacion(val texto: String, val audio: Int? = null)
}