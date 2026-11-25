package game.audio;

public enum AudioId {
    MENU_MUSIC(theme("audio/music/title.mp3")),
    WORLD_MAP(theme("audio/music/world_map.mp3")),
    PVP_MENU(theme("audio/music/pvp_menu.mp3")),
    PVP_BATTLE(theme("audio/music/pvp_battle.mp3")),
    ZEN_GARDEN(theme("audio/music/zen_garden.mp3")),
    LEVEL_MUSIC(theme("audio/music/music.mp3")),
    WIN_MUSIC(fanfare("audio/music/win.mp3")),
    LOSE_MUSIC(fanfare("audio/music/lose.mp3")),
    DEMO_VICTORY(fanfare("audio/music/demo_victory.mp3")),

    EGYPT_CHOOSE_SEEDS(theme("audio/season/ancient_egypt/choose_seeds.mp3")),
    EGYPT_LEVEL(theme("audio/season/ancient_egypt/level.mp3")),
    EGYPT_FIRST_WAVE(stinger("audio/season/ancient_egypt/first_wave.mp3")),
    EGYPT_MIDWAVE(stinger("audio/season/ancient_egypt/midwave.mp3")),
    EGYPT_FINAL_WAVE(stinger("audio/season/ancient_egypt/final_wave.mp3")),
    EGYPT_GAME_OVER(fanfare("audio/season/ancient_egypt/game_over.mp3")),
    EGYPT_VICTORY(fanfare("audio/season/ancient_egypt/victory.mp3")),
    EGYPT_ULTIMATE_BATTLE(theme("audio/season/ancient_egypt/ultimate_battle.mp3")),
    EGYPT_ZOMBOSS_INTRO(stinger("audio/season/ancient_egypt/zomboss_intro.mp3")),
    EGYPT_REWARD(stinger("audio/season/ancient_egypt/reward.mp3")),

    CAVES_CHOOSE_SEEDS(theme("audio/season/frostbite_caves/choose_seeds.mp3")),
    CAVES_LEVEL(theme("audio/season/frostbite_caves/level.mp3")),
    CAVES_FIRST_WAVE(stinger("audio/season/frostbite_caves/first_wave.mp3")),
    CAVES_MIDWAVE_A(stinger("audio/season/frostbite_caves/midwave_a.mp3")),
    CAVES_MIDWAVE_B(stinger("audio/season/frostbite_caves/midwave_b.mp3")),
    CAVES_FINAL_WAVE(stinger("audio/season/frostbite_caves/final_wave.mp3")),
    CAVES_GAME_OVER(fanfare("audio/season/frostbite_caves/game_over.mp3")),
    CAVES_VICTORY(fanfare("audio/season/frostbite_caves/victory.mp3")),
    CAVES_ULTIMATE_BATTLE(theme("audio/season/frostbite_caves/ultimate_battle.mp3")),
    CAVES_ZOMBOSS_INTRO(stinger("audio/season/frostbite_caves/zomboss_intro.mp3")),
    CAVES_REWARD(stinger("audio/season/frostbite_caves/reward.mp3")),

    BEACH_CHOOSE_SEEDS(theme("audio/season/big_wave_beach/choose_seeds.mp3")),
    BEACH_FIRST_WAVE(stinger("audio/season/big_wave_beach/first_wave.mp3")),
    BEACH_MIDWAVE_A(stinger("audio/season/big_wave_beach/midwave_a.mp3")),
    BEACH_MIDWAVE_B(stinger("audio/season/big_wave_beach/midwave_b.mp3")),
    BEACH_FINAL_WAVE(stinger("audio/season/big_wave_beach/final_wave.mp3")),
    BEACH_GAME_OVER(fanfare("audio/season/big_wave_beach/game_over.mp3")),
    BEACH_VICTORY(fanfare("audio/season/big_wave_beach/victory.mp3")),
    BEACH_ULTIMATE_BATTLE(theme("audio/season/big_wave_beach/ultimate_battle.mp3")),
    BEACH_REWARD(stinger("audio/season/big_wave_beach/reward.mp3")),

    DARK_AGES_CHOOSE_SEEDS(theme("audio/season/dark_ages/choose_seeds.mp3")),
    DARK_AGES_LEVEL(theme("audio/season/dark_ages/level.mp3")),
    DARK_AGES_FIRST_WAVE(stinger("audio/season/dark_ages/first_wave.mp3")),
    DARK_AGES_MIDWAVE_A(stinger("audio/season/dark_ages/midwave_a.mp3")),
    DARK_AGES_MIDWAVE_B(stinger("audio/season/dark_ages/midwave_b.mp3")),
    DARK_AGES_FINAL_WAVE(stinger("audio/season/dark_ages/final_wave.mp3")),
    DARK_AGES_GAME_OVER(fanfare("audio/season/dark_ages/game_over.mp3")),
    DARK_AGES_VICTORY(fanfare("audio/season/dark_ages/victory.mp3")),
    DARK_AGES_ULTIMATE_BATTLE(theme("audio/season/dark_ages/ultimate_battle.mp3")),
    DARK_AGES_ZOMBOSS_INTRO(stinger("audio/season/dark_ages/zomboss_intro.mp3")),
    DARK_AGES_REWARD(stinger("audio/season/dark_ages/reward.mp3")),

    MINIGAME_WAVES(stinger("audio/season/minigame/waves.mp3")),
    MINIGAME_GAME_OVER(fanfare("audio/season/minigame/game_over.mp3")),
    MINIGAME_VICTORY(fanfare("audio/season/minigame/victory.mp3")),
    PINATA_REWARD(fanfare("audio/season/minigame/pinata_reward.mp3")),

    CRAZY_DAVE_DIALOGUE(theme("audio/voice/crazy_dave.mp3")),
    ZOMBOSS_DIALOGUE(theme("audio/voice/zomboss.mp3")),
    ZOMBOSS_INTRO(stinger("audio/voice/zomboss_intro.mp3")),
    ZOMBOSS_PHASE_1(theme("audio/music/zomboss_phase_1.mp3")),
    ZOMBOSS_PHASE_2(theme("audio/music/zomboss_phase_2.mp3")),
    ZOMBOSS_PHASE_3(theme("audio/music/zomboss_phase_3.mp3")),
    PIANO_THEME(loopVoice("audio/music/piano.mp3")),
    REWARD_JINGLE(stinger("audio/sfx/reward_jingle.mp3")),

    ZOMBIE_DAMAGE(sfx("damage.wav", 8)),
    UI_CLICK(AudioDefinition.ui("audio/ui/click.mp3")
        .volume(1f)
        .maxConcurrent(4)
        .build()),
    UI_ERROR(AudioDefinition.ui("audio/ui/error.mp3")
        .volume(1f)
        .maxConcurrent(2)
        .build()),
    BOSS_HEALTH_INTRO(stinger("audio/voice/zomboss_intro.mp3")),

    SFX_DANCER(sfx("dancer.mp3", 2)),
    SFX_FINAL_FANFARE(sfx("finalfanfare.mp3", 1)),
    SFX_HUGE_WAVE(sfx("hugewave.mp3", 1)),
    SFX_JACK_IN_THE_BOX(sfx("jackinthebox.mp3", 2)),
    SFX_GONDOLA_INTRO(sfx("gondola_intro.mp3", 2)),
    SFX_EGYPT_STINGER_A(sfx("egypt_stinger_a.mp3", 2)),
    SFX_EGYPT_STINGER_B(sfx("egypt_stinger_b.mp3", 2)),
    SFX_THROW(sfx("throw.mp3", 16)),
    SFX_THROW2(sfx("throw2.mp3", 16)),
    SFX_THUNDER(sfx("thunder.mp3", 4)),
    SFX_VASE_BREAKING(sfx("vase_breaking.mp3", 4)),
    SFX_ZOMBIE_ENTERING_WATER(sfx("zombie_entering_water.mp3", 4)),
    SFX_ZOMBIE_SPLASH(sfx("zombie_splash.mp3", 4)),
    SFX_BOWLING(sfx("bowling.mp3", 4)),
    SFX_BOWLING_IMPACT(sfx("bowling_impact.mp3", 6)),
    SFX_BOWLING_IMPACT2(sfx("bowling_impact2.mp3", 6)),
    SFX_BUTTER(sfx("butter.mp3", 6)),
    SFX_CHERRYBOMB(sfx("cherrybomb.mp3", 3)),
    SFX_COIN(sfx("coin.mp3", 8)),
    SFX_DIRT_RISE(sfx("dirt_rise.mp3", 6)),
    SFX_DOOMSHROOM(sfx("doomshroom.mp3", 2)),
    SFX_EXPLOSION(sfx("explosion.mp3", 6)),
    SFX_FINAL_WAVE(sfx("finalwave.mp3", 1)),
    SFX_FROZEN(sfx("frozen.mp3", 4)),
    SFX_FUME(sfx("fume.mp3", 8)),
    SFX_GARGANTUAR_THUMP(sfx("gargantuar_thump.mp3", 4)),
    SFX_GRASSSTEP(sfx("grassstep.mp3", 8)),
    SFX_GRAVEBUSTERCHOMP(sfx("gravebusterchomp.mp3", 2)),
    SFX_GRAVEBUTTON(sfx("gravebutton.mp3", 4)),
    SFX_GRAVESTONE_RUMBLE(sfx("gravestone_rumble.mp3", 4)),
    SFX_HYDRAULIC(sfx("hydraulic.mp3", 3)),
    SFX_HYDRAULIC_SHORT(sfx("hydraulic_short.mp3", 4)),
    SFX_IGNITE(sfx("ignite.mp3", 8)),
    SFX_IGNITE2(sfx("ignite2.mp3", 8)),
    SFX_JALAPENO(sfx("jalapeno.mp3", 2)),
    SFX_LAWNMOWER(sfx("lawnmower.mp3", 3)),
    SFX_LIGHTFILL(sfx("lightfill.mp3", 4)),
    SFX_LIMBS_POP(sfx("limbs_pop.mp3", 8)),
    SFX_MAGNETSHROOM(sfx("magnetshroom.mp3", 3)),
    SFX_MONEYFALLS(sfx("moneyfalls.mp3", 8)),
    SFX_NEWSPAPER_RIP(sfx("newspaper_rip.mp3", 2)),
    SFX_PLANT_WATER(sfx("plant_water.mp3", 6)),
    SFX_PLANTERN(sfx("plantern.mp3", 4)),
    SFX_PLANTGROW(sfx("plantgrow.mp3", 8)),
    SFX_POGO_ZOMBIE(sfx("pogo_zombie.mp3", 4)),
    SFX_POINTS(sfx("points.mp3", 8)),
    SFX_PORTAL(sfx("portal.mp3", 3)),
    SFX_POTATO_MINE(sfx("potato_mine.mp3", 3)),
    SFX_READYSETPLANT(sfx("readysetplant.mp3", 1)),
    SFX_SEEDLIFT(sfx("seedlift.mp3", 6)),
    SFX_SHIELDHIT(sfx("shieldhit.mp3", 8)),
    SFX_SHIELDHIT2(sfx("shieldhit2.mp3", 8)),
    SFX_SHOVEL(sfx("shovel.mp3", 4)),
    SFX_SNOW_PEA_SPARKLES(sfx("snow_pea_sparkles.mp3", 8)),
    SFX_PAUSE(sfx("pause.mp3", 2)),
    SFX_NIGHT(AudioDefinition.sfx("audio/sfx/night.mp3").volume(0.7f).maxConcurrent(1).lazy().build()),

    VOX_DAVE_CRAZY(sfx("dave_crazy.mp3", 1)),
    VOX_DAVE_EXTRALONG1(sfx("dave_extralong1.mp3", 1)),
    VOX_DAVE_EXTRALONG2(sfx("dave_extralong2.mp3", 1)),
    VOX_DAVE_EXTRALONG3(sfx("dave_extralong3.mp3", 1)),
    VOX_DAVE_LONG1(sfx("dave_long1.mp3", 1)),
    VOX_DAVE_LONG2(sfx("dave_long2.mp3", 1)),
    VOX_DAVE_LONG3(sfx("dave_long3.mp3", 1)),
    VOX_DAVE_SCREAM(sfx("dave_scream.mp3", 1)),
    VOX_DAVE_SCREAM2(sfx("dave_scream2.mp3", 1)),
    VOX_DAVE_SHORT1(sfx("dave_short1.mp3", 1)),
    VOX_DAVE_SHORT2(sfx("dave_short2.mp3", 1)),
    VOX_DAVE_SHORT3(sfx("dave_short3.mp3", 1)),
    VOX_DOLPHIN_APPEARS(sfx("dolphin_appears.mp3", 3)),
    VOX_DOLPHIN_JUMP(sfx("dolphin_jump.mp3", 3)),
    VOX_EVILLAUGH(sfx("evillaugh.mp3", 3)),
    VOX_GARGANTUAR_DEATH(sfx("gargantuar_death.mp3", 2)),
    VOX_GROAN1(sfx("groan1.mp3", 4)),
    VOX_GROAN2(sfx("groan2.mp3", 4)),
    VOX_GROAN3(sfx("groan3.mp3", 4)),
    VOX_GROAN4(sfx("groan4.mp3", 4)),
    VOX_GROAN5(sfx("groan5.mp3", 4)),
    VOX_GROAN6(sfx("groan6.mp3", 4)),
    VOX_GULP(sfx("gulp.mp3", 3)),
    VOX_IMP(sfx("imp.mp3", 4)),
    VOX_IMP2(sfx("imp2.mp3", 4)),
    VOX_JACK_SURPRISE(sfx("jack_surprise.mp3", 3)),
    VOX_JACK_SURPRISE2(sfx("jack_surprise2.mp3", 3)),
    VOX_LOADINGBAR_ZOMBIE(sfx("loadingbar_zombie.mp3", 2)),
    VOX_LOWGROAN(sfx("lowgroan.mp3", 4)),
    VOX_LOWGROAN2(sfx("lowgroan2.mp3", 4)),
    VOX_NEWSPAPER_RARRGH(sfx("newspaper_rarrgh.mp3", 2)),
    VOX_NEWSPAPER_RARRGH2(sfx("newspaper_rarrgh2.mp3", 2)),
    VOX_SCREAM(sfx("scream.mp3", 3)),
    VOX_SQUASH_HMM(sfx("squash_hmm.mp3", 2)),
    VOX_SQUASH_HMM2(sfx("squash_hmm2.mp3", 2)),
    VOX_SUKHBIR(sfx("sukhbir.mp3", 4)),
    VOX_SUKHBIR5(sfx("sukhbir5.mp3", 4)),
    VOX_SUKHBIR6(sfx("sukhbir6.mp3", 4)),
    VOX_YUCK(sfx("yuck.mp3", 4)),
    VOX_YUCK2(sfx("yuck2.mp3", 4)),
    VOX_ZOMBAQUARIUM_DIE(sfx("zombaquarium_die.mp3", 4)),


    SFX_ICEBERGLETTUCE_1(sfx("2-256. VO ICEBERGLETTUCE 1.mp3")),
    SFX_ICEBERGLETTUCE_2(sfx("2-257. VO ICEBERGLETTUCE 2.mp3")),

    SFX_PEASHOOTER_1(sfx("2-260. VO PEASHOOTER 1.mp3")),
    SFX_PEASHOOTER_2(sfx("2-261. VO PEASHOOTER 2.mp3")),
    SFX_PEASHOOTER_3(sfx("2-262. VO PEASHOOTER 3.mp3")),
    SFX_PEASHOOTER_4(sfx("2-263. VO PEASHOOTER 4.mp3")),
    SFX_PEASHOOTER_5(sfx("2-264. VO PEASHOOTER 5.mp3")),
    SFX_PEASHOOTER_6(sfx("2-265. VO PEASHOOTER 6.mp3")),

    SFX_SUNFLOWER_1(sfx("2-267. VO SUNFLOWER 1.mp3")),
    SFX_SUNFLOWER_2(sfx("2-268. VO SUNFLOWER 2.mp3")),
    SFX_SUNFLOWER_3(sfx("2-269. VO SUNFLOWER 3.mp3")),
    SFX_SUNFLOWER_4(sfx("2-270. VO SUNFLOWER 4.mp3")),
    SFX_SUNFLOWER_5(sfx("2-271. VO SUNFLOWER 5.mp3")),

    SFX_BONKCHOY_1(sfx("4-0206. PLANT BONKCHOY 1.mp3")),
    SFX_BONKCHOY_2(sfx("4-0207. PLANT BONKCHOY 2.mp3")),
    SFX_BONKCHOY_3(sfx("4-0208. PLANT BONKCHOY 3.mp3")),

    SFX_BOWLINGBULB_1(sfx("4-0222. PLANT BOWLINGBULB 1.mp3")),
    SFX_BOWLINGBULB_10(sfx("4-0223. PLANT BOWLINGBULB 10.mp3")),
    SFX_BOWLINGBULB_11(sfx("4-0224. PLANT BOWLINGBULB 11.mp3")),
    SFX_BOWLINGBULB_12(sfx("4-0225. PLANT BOWLINGBULB 12.mp3")),
    SFX_BOWLINGBULB_14(sfx("4-0227. PLANT BOWLINGBULB 14.mp3")),
    SFX_BOWLINGBULB_15(sfx("4-0228. PLANT BOWLINGBULB 15.mp3")),
    SFX_BOWLINGBULB_16(sfx("4-0229. PLANT BOWLINGBULB 16.mp3")),
    SFX_BOWLINGBULB_18(sfx("4-0231. PLANT BOWLINGBULB 18.mp3")),
    SFX_BOWLINGBULB_21(sfx("4-0235. PLANT BOWLINGBULB 21.mp3")),
    SFX_BOWLINGBULB_23(sfx("4-0237. PLANT BOWLINGBULB 23.mp3")),
    SFX_BOWLINGBULB_24(sfx("4-0238. PLANT BOWLINGBULB 24.mp3")),

    SFX_CABBAGEPULT_1(sfx("4-0267. PLANT CABBAGEPULT 1.mp3")),
    SFX_CABBAGEPULT_2(sfx("4-0268. PLANT CABBAGEPULT 2.mp3")),


    SFX_CABBAGEPULT_3(sfx("4-0269. PLANT CABBAGEPULT 3.mp3")),
    SFX_CABBAGEPULT_4(sfx("4-0270. PLANT CABBAGEPULT 4.mp3")),
    SFX_CABBAGEPULT_5(sfx("4-0271. PLANT CABBAGEPULT 5.mp3")),

    SFX_CACTUS_1(sfx("4-0275. PLANT CACTUS 1.mp3")),
    SFX_CACTUS_10(sfx("4-0276. PLANT CACTUS 10.mp3")),
    SFX_CACTUS_11(sfx("4-0277. PLANT CACTUS 11.mp3")),
    SFX_CACTUS_12(sfx("4-0278. PLANT CACTUS 12.mp3")),
    SFX_CACTUS_13(sfx("4-0279. PLANT CACTUS 13.mp3")),
    SFX_CACTUS_14(sfx("4-0280. PLANT CACTUS 14.mp3")),
    SFX_CACTUS_15(sfx("4-0281. PLANT CACTUS 15.mp3")),
    SFX_CACTUS_16(sfx("4-0282. PLANT CACTUS 16.mp3")),
    SFX_CACTUS_17(sfx("4-0283. PLANT CACTUS 17.mp3")),

    SFX_CAULIPOWER_1(sfx("4-0302. PLANT CAULIPOWER 1.mp3")),
    SFX_CAULIPOWER_10(sfx("4-0303. PLANT CAULIPOWER 10.mp3")),
    SFX_CAULIPOWER_2(sfx("4-0304. PLANT CAULIPOWER 2.mp3")),
    SFX_CAULIPOWER_3(sfx("4-0305. PLANT CAULIPOWER 3.mp3")),
    SFX_CAULIPOWER_4(sfx("4-0306. PLANT CAULIPOWER 4.mp3")),
    SFX_CAULIPOWER_5(sfx("4-0307. PLANT CAULIPOWER 5.mp3")),
    SFX_CAULIPOWER_6(sfx("4-0308. PLANT CAULIPOWER 6.mp3")),

    SFX_CHERRYBOMB_1(sfx("4-0373. PLANT CHERRYBOMB 1.mp3")),

    SFX_CHOMPER_1(sfx("4-0379. PLANT CHOMPER 1.mp3")),
    SFX_CHOMPER_10(sfx("4-0380. PLANT CHOMPER 10.mp3")),
    SFX_CHOMPER_11(sfx("4-0381. PLANT CHOMPER 11.mp3")),
    SFX_CHOMPER_12(sfx("4-0382. PLANT CHOMPER 12.mp3")),

    SFX_CITRON_1(sfx("4-0397. PLANT CITRON 1.mp3")),
    SFX_CITRON_10(sfx("4-0398. PLANT CITRON 10.mp3")),
    SFX_CITRON_3(sfx("4-0403. PLANT CITRON 3.mp3")),
    SFX_CITRON_4(sfx("4-0404. PLANT CITRON 4.mp3")),
    SFX_CITRON_5(sfx("4-0405. PLANT CITRON 5.mp3")),
    SFX_CITRON_6(sfx("4-0406. PLANT CITRON 6.mp3")),

    SFX_CITRON_7(sfx("4-0407. PLANT CITRON 7.mp3")),
    SFX_CITRON_8(sfx("4-0408. PLANT CITRON 8.mp3")),
    SFX_CITRON_9(sfx("4-0409. PLANT CITRON 9.mp3")),

    SFX_DOOMSHROOM_1(sfx("4-0502. PLANT DOOMSHROOM 1.mp3")),
    SFX_DOOMSHROOM_2(sfx("4-0503. PLANT DOOMSHROOM 2.mp3")),
    SFX_DOOMSHROOM_3(sfx("4-0504. PLANT DOOMSHROOM 3.mp3")),

    SFX_ELECTRICBLUEBERRY_1(sfx("4-0548. PLANT ELECTRICBLUEBERRY 1.mp3")),
    SFX_ELECTRICBLUEBERRY_10(sfx("4-0549. PLANT ELECTRICBLUEBERRY 10.mp3")),
    SFX_ELECTRICBLUEBERRY_11(sfx("4-0550. PLANT ELECTRICBLUEBERRY 11.mp3")),
    SFX_ELECTRICBLUEBERRY_12(sfx("4-0551. PLANT ELECTRICBLUEBERRY 12.mp3")),
    SFX_ELECTRICBLUEBERRY_13(sfx("4-0552. PLANT ELECTRICBLUEBERRY 13.mp3")),
    SFX_ELECTRICBLUEBERRY_14(sfx("4-0553. PLANT ELECTRICBLUEBERRY 14.mp3")),

    SFX_ENDURIAN_3(sfx("4-0705. PLANT ENDURIAN 3.mp3")),

    SFX_EXPLODEONUT_1(sfx("4-0751. PLANT EXPLODEONUT 1.mp3")),
    SFX_EXPLODEONUT_2(sfx("4-0752. PLANT EXPLODEONUT 2.mp3")),
    SFX_EXPLODEONUT_3(sfx("4-0753. PLANT EXPLODEONUT 3.mp3")),
    SFX_EXPLODEONUT_4(sfx("4-0754. PLANT EXPLODEONUT 4.mp3")),
    SFX_EXPLODEONUT_5(sfx("4-0755. PLANT EXPLODEONUT 5.mp3")),
    SFX_EXPLODEONUT_6(sfx("4-0756. PLANT EXPLODEONUT 6.mp3")),
    SFX_EXPLODEONUT_8(sfx("4-0758. PLANT EXPLODEONUT 8.mp3")),

    SFX_FIREPEASHOOTER_1(sfx("4-0771. PLANT FIREPEASHOOTER 1.mp3")),
    SFX_FIREPEASHOOTER_10(sfx("4-0772. PLANT FIREPEASHOOTER 10.mp3")),
    SFX_FIREPEASHOOTER_11(sfx("4-0773. PLANT FIREPEASHOOTER 11.mp3")),
    SFX_FIREPEASHOOTER_13(sfx("4-0775. PLANT FIREPEASHOOTER 13.mp3")),
    SFX_FIREPEASHOOTER_2(sfx("4-0776. PLANT FIREPEASHOOTER 2.mp3")),
    SFX_FIREPEASHOOTER_3(sfx("4-0777. PLANT FIREPEASHOOTER 3.mp3")),
    SFX_FIREPEASHOOTER_5(sfx("4-0779. PLANT FIREPEASHOOTER 5.mp3")),
    SFX_FIREPEASHOOTER_6(sfx("4-0780. PLANT FIREPEASHOOTER 6.mp3")),
    SFX_FIREPEASHOOTER_7(sfx("4-0781. PLANT FIREPEASHOOTER 7.mp3")),
    SFX_FIREPEASHOOTER_8(sfx("4-0782. PLANT FIREPEASHOOTER 8.mp3")),

    SFX_FUMESHROOM_1(sfx("4-0793. PLANT FUMESHROOM 1.mp3")),
    SFX_FUMESHROOM_2(sfx("4-0794. PLANT FUMESHROOM 2.mp3")),

    SFX_GARLIC_1(sfx("4-0795. PLANT GARLIC 1.mp3")),


    SFX_PEPPERPULT_2(sfx("4-1363. PLANT PEPPERPULT 2.mp3")),
    SFX_PEPPERPULT_3(sfx("4-1364. PLANT PEPPERPULT 3.mp3")),

    SFX_PHATBEET_1(sfx("4-1377. PLANT PHATBEET 1.mp3")),
    SFX_PHATBEET_2(sfx("4-1378. PLANT PHATBEET 2.mp3")),
    SFX_PHATBEET_4(sfx("4-1380. PLANT PHATBEET 4.mp3")),
    SFX_PHATBEET_6(sfx("4-1382. PLANT PHATBEET 6.mp3")),

    SFX_POTATOMINE_1(sfx("4-1399. PLANT POTATOMINE 1.mp3")),
    SFX_POTATOMINE_2(sfx("4-1400. PLANT POTATOMINE 2.mp3")),
    SFX_POTATOMINE_3(sfx("4-1401. PLANT POTATOMINE 3.mp3")),

    SFX_PUFFSHROOM_1(sfx("4-1443. PLANT PUFFSHROOM 1.mp3")),
    SFX_PUFFSHROOM_2(sfx("4-1444. PLANT PUFFSHROOM 2.mp3")),
    SFX_PUFFSHROOM_3(sfx("4-1445. PLANT PUFFSHROOM 3.mp3")),
    SFX_PUFFSHROOM_4(sfx("4-1446. PLANT PUFFSHROOM 4.mp3")),
    SFX_PUFFSHROOM_5(sfx("4-1447. PLANT PUFFSHROOM 5.mp3")),

    SFX_PUMPKIN_1(sfx("4-1449. PLANT PUMPKIN 1.mp3")),
    SFX_PUMPKIN_2(sfx("4-1450. PLANT PUMPKIN 2.mp3")),
    SFX_PUMPKIN_3(sfx("4-1451. PLANT PUMPKIN 3.mp3")),

    SFX_REPEATER_1(sfx("4-1477. PLANT REPEATER 1.mp3")),
    SFX_REPEATER_2(sfx("4-1478. PLANT REPEATER 2.mp3")),
    SFX_REPEATER_3(sfx("4-1479. PLANT REPEATER 3.mp3")),

    SFX_SEASHROOM_1(sfx("4-1557. PLANT SEASHROOM 1.mp3")),
    SFX_SEASHROOM_2(sfx("4-1558. PLANT SEASHROOM 2.mp3")),
    SFX_SEASHROOM_3(sfx("4-1559. PLANT SEASHROOM 3.mp3")),
    SFX_SEASHROOM_4(sfx("4-1560. PLANT SEASHROOM 4.mp3")),
    SFX_SEASHROOM_5(sfx("4-1561. PLANT SEASHROOM 5.mp3")),
    SFX_SEASHROOM_6(sfx("4-1562. PLANT SEASHROOM 6.mp3")),

    SFX_SNOWPEA_1(sfx("4-1629. PLANT SNOWPEA 1.mp3")),
    SFX_SNOWPEA_2(sfx("4-1630. PLANT SNOWPEA 2.mp3")),
    SFX_SNOWPEA_3(sfx("4-1631. PLANT SNOWPEA 3.mp3")),
    SFX_SNOWPEA_4(sfx("4-1632. PLANT SNOWPEA 4.mp3")),

    SFX_SPLITPEA_1(sfx("4-1698. PLANT SPLITPEA 1.mp3")),
    SFX_SPLITPEA_2(sfx("4-1699. PLANT SPLITPEA 2.mp3")),
    SFX_SPLITPEA_3(sfx("4-1700. PLANT SPLITPEA 3.mp3")),


    SFX_WALLNUT_DEFAULT(sfx("2-292. VO WALLNUT.mp3")),
    SFX_WALLNUT_1(sfx("2-293. VO WALLNUT 1.mp3")),
    SFX_SQUASH_1(sfx("4-1734. PLANT SQUASH 1.mp3")),
    SFX_SQUASH_10(sfx("4-1735. PLANT SQUASH 10.mp3")),
    SFX_SQUASH_11(sfx("4-1736. PLANT SQUASH 11.mp3")),
    SFX_SQUASH_12(sfx("4-1737. PLANT SQUASH 12.mp3")),
    SFX_SQUASH_13(sfx("4-1738. PLANT SQUASH 13.mp3")),
    SFX_SQUASH_15(sfx("4-1740. PLANT SQUASH 15.mp3")),
    SFX_STARFRUIT_1(sfx("4-1755. PLANT STARFRUIT 1.mp3")),
    SFX_STARFRUIT_2(sfx("4-1756. PLANT STARFRUIT 2.mp3")),
    SFX_STARFRUIT_3(sfx("4-1757. PLANT STARFRUIT 3.mp3")),
    SFX_STARFRUIT_4(sfx("4-1758. PLANT STARFRUIT 4.mp3")),
    SFX_SUNSHROOM_1(sfx("4-1823. PLANT SUNSHROOM 1.mp3")),
    SFX_SUNSHROOM_2(sfx("4-1824. PLANT SUNSHROOM 2.mp3")),
    SFX_SUNSHROOM_3(sfx("4-1825. PLANT SUNSHROOM 3.mp3")),
    SFX_SUNSHROOM_4(sfx("4-1826. PLANT SUNSHROOM 4.mp3")),
    SFX_TALLNUT_1(sfx("4-1857. PLANT TALLNUT 1.mp3")),
    SFX_TANGLEKELP_1(sfx("4-1858. PLANT TANGLEKELP 1.mp3")),
    SFX_TANGLEKELP_10(sfx("4-1859. PLANT TANGLEKELP 10.mp3")),
    SFX_TANGLEKELP_11(sfx("4-1860. PLANT TANGLEKELP 11.mp3")),
    SFX_TANGLEKELP_12(sfx("4-1861. PLANT TANGLEKELP 12.mp3")),
    SFX_TANGLEKELP_13(sfx("4-1862. PLANT TANGLEKELP 13.mp3")),
    SFX_TANGLEKELP_14(sfx("4-1863. PLANT TANGLEKELP 14.mp3")),
    SFX_THREEPEATER_1(sfx("4-1901. PLANT THREEPEATER 1.mp3")),
    SFX_THREEPEATER_2(sfx("4-1902. PLANT THREEPEATER 2.mp3")),
    SFX_TORCHWOOD_1(sfx("4-1959. PLANT TORCHWOOD 1.mp3")),
    SFX_TORCHWOOD_2(sfx("4-1960. PLANT TORCHWOOD 2.mp3")),
    SFX_TORCHWOOD_3(sfx("4-1961. PLANT TORCHWOOD 3.mp3")),
    SFX_TORCHWOOD_4(sfx("4-1962. PLANT TORCHWOOD 4.mp3")),
    SFX_WASABIWHIP_1(sfx("4-2041. PLANT WASABIWHIP 1.mp3")),
    SFX_WASABIWHIP_14(sfx("4-2046. PLANT WASABIWHIP 14.mp3")),
    SFX_WASABIWHIP_19(sfx("4-2051. PLANT WASABIWHIP 19.mp3")),
    SFX_WASABIWHIP_2(sfx("4-2052. PLANT WASABIWHIP 2.mp3")),
    SFX_WINTERMELON_1(sfx("4-2093. PLANT WINTERMELON 1.mp3")),
    SFX_WINTERMELON_2(sfx("4-2094. PLANT WINTERMELON 2.mp3")),
    SFX_WINTERMELON_3(sfx("4-2095. PLANT WINTERMELON 3.mp3")),
    SFX_WINTERMELON_4(sfx("4-2096. PLANT WINTERMELON 4.mp3")),
    SFX_WINTERMELON_5(sfx("4-2097. PLANT WINTERMELON 5.mp3")),
    SFX_WINTERMELON_6(sfx("4-2098. PLANT WINTERMELON 6.mp3")),


    ;

    private final AudioDefinition definition;

    AudioId(AudioDefinition definition) {
        this.definition = definition;
    }

    public AudioDefinition definition() {
        return definition;
    }

    private static AudioDefinition theme(String path) {
        return AudioDefinition.music(path).volume(1f).build();
    }

    private static AudioDefinition fanfare(String path) {
        return AudioDefinition.music(path).repeat(1).volume(1f).build();
    }

    private static AudioDefinition stinger(String path) {
        return AudioDefinition.voice(path).volume(0.95f).repeat(1).build();
    }

    private static AudioDefinition loopVoice(String path) {
        return AudioDefinition.voice(path).looping().volume(0.9f).build();
    }

    private static AudioDefinition sfx(String file) {
        return sfx(file, 8);
    }

    private static AudioDefinition sfx(String file, int maxConcurrent) {
        return AudioDefinition.sfx("audio/sfx/" + file)
            .volume(1f)
            .maxConcurrent(maxConcurrent)
            .build();
    }
}
