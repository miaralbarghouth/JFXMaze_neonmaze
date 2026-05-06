package com.mazegame.ui;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import java.util.HashMap;
import java.util.Map;

public class LanguageManager {
    public enum Language { EN, TR }
    
    private static final StringProperty currentLanguage = new SimpleStringProperty(Language.EN.name());
    private static final Map<String, Map<Language, String>> translations = new HashMap<>();

    static {
        addTranslation("login.title", "NEON MAZE", "NEON LABIRENT");
        addTranslation("login.username", "Username", "Kullanici Adi");
        addTranslation("login.password", "Password", "Sifre");
        addTranslation("login.button", "Login", "Giris Yap");
        addTranslation("login.error", "Invalid username or password!", "Hatali kullanici adi veya sifre!");
        addTranslation("login.noAccount", "Do not have an account? ", "Hesabin yoksa ");
        addTranslation("login.createLink", "Create Account", "hesap olustur");
        
        addTranslation("register.title", "Create Account", "Hesap Olustur");
        addTranslation("register.confirmPassword", "Confirm Password", "Sifre Tekrar");
        addTranslation("register.button", "Register", "Kayit Ol");
        addTranslation("register.error.match", "Passwords do not match!", "Sifreler uyusmuyor!");
        addTranslation("register.error.empty", "Fields cannot be empty!", "Alanlar bos birakilamaz!");
        addTranslation("register.error.exists", "Username already exists!", "Bu kullanici adi zaten kayitli!");

        addTranslation("menu.start", "Start Game", "Oyuna Basla");
        addTranslation("menu.settings", "Settings", "Ayarlar");
        addTranslation("menu.leaderboard", "Leaderboard", "Liderlik Tablosu");
        addTranslation("menu.exit", "Exit", "Cikis");
        addTranslation("menu.logout", "Logout", "Hesaptan Cikis Yap");
        
        addTranslation("settings.title", "SETTINGS", "AYARLAR");
        addTranslation("settings.masterVol", "Master Volume", "Ana Ses");
        addTranslation("settings.musicVol", "Music Volume", "Muzik Ses");
        addTranslation("settings.musicToggle", "MUSIC: ", "MUZIK: ");
        addTranslation("settings.on", "ON", "ACIK");
        addTranslation("settings.off", "OFF", "KAPALI");
        addTranslation("settings.language", "Language", "Dil");
        addTranslation("settings.mazeSize", "Maze Size", "Labirent Boyutu");
        addTranslation("settings.controls", "Controls", "Oyun Tuslari");
        addTranslation("settings.credits", "Credits", "Yapimci");
        addTranslation("settings.account", "Account Settings", "Hesap Ayarlari");
        addTranslation("settings.changeUser", "Change Username", "Isim Degistir");
        addTranslation("settings.changePass", "Change Password", "Sifre Degistir");
        addTranslation("settings.deleteAcc", "Delete Account", "Hesabi Sil");
        addTranslation("settings.newUser", "New Username", "Yeni Kullanici Adi");
        addTranslation("settings.newPass", "New Password", "Yeni Sifre");
        addTranslation("settings.save", "SAVE", "KAYDET");
        addTranslation("settings.update", "Update", "Guncelle");
        addTranslation("settings.back", "BACK", "GERI");
        addTranslation("settings.creditsMsg",
            "Developed by Miar Albarghouth\nBuilt with JavaFX and VS Code\nFocused on algorithmic maze design and futuristic UI/UX.",
            "Miar Albarghouth tarafindan gelistirilmistir\nJavaFX ve VS Code kullanilarak tasarlanmistir\nAlgoritmik labirent tasarimi ve futuristik kullanici deneyimine odaklanilmistir.");
        addTranslation("settings.controlsMsg", "WASD or Arrows: Move\nH: Hint\nESC: Pause", "WASD veya Ok Tuslari: Hareket\nH: Ipucu\nESC: Duraklat");
        
        addTranslation("game.score", "Score: ", "Puan: ");
        addTranslation("game.steps", "Steps: ", "Adim: ");
        addTranslation("game.time", "Time: ", "Sure: ");
        addTranslation("game.congrats", "CONGRATULATIONS", "TEBRIKLER");
        addTranslation("game.winMessage", "YOU REACHED THE EXIT!", "CIKISA ULASTINIZ!");
        addTranslation("game.restart", "Play Again", "Tekrar Oyna");
        addTranslation("game.menu", "Main Menu", "Ana Menu");
        addTranslation("game.saveRecord", "Save Record", "Rekoru Kaydet");
        addTranslation("game.recordSaved", "Record Saved!", "Rekor Kaydedildi!");

        addTranslation("pause.title", "PAUSED", "DURAKLATILDI");
        addTranslation("pause.resume", "Resume", "Devam Et");
        addTranslation("pause.restart", "Restart Game", "Oyunu Sifirla");
        addTranslation("pause.settings", "Settings", "Ayarlar");
        addTranslation("pause.exit", "Exit", "Cikis");

        addTranslation("leaderboard.title", "Leaderboard", "Liderlik Tablosu");
        addTranslation("leaderboard.empty", "No records yet.", "Henuz rekor yok.");

        addTranslation("game.size.title", "CHOOSE MAZE SIZE", "LABIRENT BOYUTU SEC");
        addTranslation("game.size.play", "PLAY", "OYNAT");
        addTranslation("game.size.cancel", "CANCEL", "IPTAL");
        addTranslation("game.size.current", "Size: ", "Boyut: ");
    }

    private static void addTranslation(String key, String en, String tr) {
        Map<Language, String> map = new HashMap<>();
        map.put(Language.EN, en);
        map.put(Language.TR, tr);
        translations.put(key, map);
    }

    public static String get(String key) {
        Language lang = Language.valueOf(currentLanguage.get());
        Map<Language, String> map = translations.get(key);
        if (map == null) return key;
        return map.getOrDefault(lang, key);
    }

    public static StringProperty getStringProperty(String key) {
        SimpleStringProperty property = new SimpleStringProperty(get(key));
        currentLanguage.addListener((obs, oldVal, newVal) -> property.set(get(key)));
        return property;
    }

    public static void setLanguage(Language lang) {
        currentLanguage.set(lang.name());
    }

    public static Language getLanguage() {
        return Language.valueOf(currentLanguage.get());
    }
}
