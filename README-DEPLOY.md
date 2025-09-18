# 🚀 Guida al Deploy su Render con Cloudinary

## Prerequisiti

1. **Account Render.com** - Registrati gratuitamente su https://render.com
2. **Account Cloudinary** - Registrati gratuitamente su https://cloudinary.com  
3. **Repository GitHub** - Il codice deve essere su GitHub

## Setup Cloudinary

### 1. Ottieni le credenziali Cloudinary

1. Vai su https://cloudinary.com e registrati
2. Nel Dashboard, trova le credenziali:
   - **Cloud Name**: es. `dxyzabc123`
   - **API Key**: es. `123456789012345`
   - **API Secret**: es. `abcdef1234567890abcdef1234567890`

## Deploy su Render

### 1. Connetti il Repository

1. Vai su https://render.com e fai login
2. Clicca su **"New +"** → **"Web Service"**
3. Connetti il tuo account GitHub
4. Seleziona il repository del progetto QR Code Manager

### 2. Configurazione del Servizio

**Impostazioni di base:**
- **Name**: `qr-code-manager`
- **Region**: `Frankfurt (EU Central)`
- **Branch**: `main` (o il tuo branch principale)
- **Runtime**: `Java`

**Comandi di build:**
- **Build Command**: `./mvnw clean install -DskipTests`
- **Start Command**: `java -Dserver.port=$PORT -jar target/qr-code-manager-0.0.1-SNAPSHOT.jar`

### 3. Variabili d'Ambiente

Nel pannello **Environment**, aggiungi queste variabili:

```bash
# Profilo Spring Boot
SPRING_PROFILES_ACTIVE=production

# Cloudinary (OBBLIGATORIE)
CLOUDINARY_CLOUD_NAME=il_tuo_cloud_name
CLOUDINARY_API_KEY=la_tua_api_key  
CLOUDINARY_API_SECRET=il_tuo_api_secret

# Sicurezza JWT (lascia che Render la generi)
JWT_SECRET=[Auto-generated]

# Admin iniziale
ADMIN_EMAIL=admin@qrmanager.com
ADMIN_PASSWORD=TuaPasswordSicura123!
```

**⚠️ IMPORTANTE**: Sostituisci i valori Cloudinary con quelli reali dal tuo account!

### 4. Deploy

1. Clicca **"Create Web Service"**
2. Render inizierà automaticamente il build e deploy
3. Il primo deploy richiede 5-10 minuti

## Post-Deploy

### 1. Verifica il Funzionamento

1. Vai all'URL fornito da Render (es. `https://qr-code-manager.onrender.com`)
2. Accedi con:
   - **Email**: `admin@qrmanager.com` 
   - **Password**: quella che hai impostato in `ADMIN_PASSWORD`

### 2. Cambia la Password Admin

**IMPORTANTE**: Cambia immediatamente la password dell'admin dopo il primo accesso!

### 3. Test delle Funzionalità

1. **Dashboard Admin**: Verifica che le statistiche si carichino
2. **Crea QR Code**: Testa la creazione di un nuovo QR code
3. **Upload Immagini**: Prova ad aggiungere un articolo con foto
4. **Scansione QR**: Testa l'accesso pubblico al QR code

## Configurazione Avanzata

### Custom Domain (Opzionale)

1. Nel pannello Render, vai su **Settings** → **Custom Domains**
2. Aggiungi il tuo dominio personalizzato
3. Configura i DNS secondo le istruzioni di Render

### Monitoring

- **Health Check**: Render controlla automaticamente `/actuator/health`
- **Logs**: Visualizzabili nel pannello Render
- **Metrics**: Disponibili nella sezione monitoring

## Troubleshooting

### Problemi Comuni

**❌ Build fallisce:**
```bash
# Verifica che il file mvnw sia eseguibile
chmod +x mvnw
```

**❌ Cloudinary non funziona:**
- Controlla che le credenziali siano corrette
- Verifica che non ci siano spazi extra nelle variabili

**❌ Database H2 si resetta:**
- Normale in caso di restart del servizio gratuito
- Per persistenza garantita, considera upgrade a piano a pagamento

**❌ App lenta al primo avvio:**
- Normale per il piano gratuito di Render
- Il servizio va in "sleep" dopo inattività

### Log Utili

```bash
# Per vedere i log dell'applicazione
# Vai su Render Dashboard → il tuo servizio → Logs
```

## Sicurezza

### Variabili d'Ambiente Sensibili

- **Non commitare mai** credenziali nel codice
- Usa sempre le Environment Variables di Render
- Cambia periodicamente JWT_SECRET e password admin

### SSL/HTTPS

- Render fornisce automaticamente certificati SSL gratuiti
- Tutte le connessioni sono criptate HTTPS

## Costi

### Piano Gratuito Render

- **750 ore/mese** di runtime
- **Limitazioni**: 
  - Sleep dopo 15 min di inattività
  - 512MB RAM
  - Build pubblici

### Piano Gratuito Cloudinary

- **25 GB** storage
- **25 GB** bandwidth mensile
- **1000** trasformazioni/mese

## Backup

### Database H2

Il piano gratuito non garantisce persistenza. Per production:
1. Considera PostgreSQL addon di Render
2. O esporta periodicamente i dati

### Immagini

- Cloudinary mantiene automaticamente le immagini
- Considera backup periodici per sicurezza

---

## 🎉 Congratulazioni!

La tua applicazione QR Code Manager è ora live su Render con storage Cloudinary professionale!

**URL Applicazione**: `https://il-tuo-servizio.onrender.com`

**Prossimi Passi**:
1. Personalizza il dominio  
2. Configura monitoring avanzato
3. Implementa backup automatici
4. Considera upgrade per production