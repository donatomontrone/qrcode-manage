# 🎯 QR Code Manager

Sistema completo per la gestione di QR code personalizzati con articoli associati, sviluppato con Spring Boot e Thymeleaf.

## 🚀 Caratteristiche

- **Gestione QR Code**: Creazione e gestione QR code con scadenza e limiti articoli
- **Upload Immagini**: Integrazione con Cloudinary per storage scalabile
- **Dashboard Admin**: Interface moderna e responsive
- **Autenticazione**: Spring Security con ruoli gerarchici
- **Mobile-First**: Ottimizzato per smartphone e tablet

## 🛠️ Tecnologie

- **Backend**: Java 17 + Spring Boot 3.2.1
- **Frontend**: Thymeleaf + Tailwind CSS
- **Database**: H2 Database
- **Security**: Spring Security + JWT
- **QR Generation**: ZXing Library
- **Image Storage**: Cloudinary
- **Deploy**: Render.com

## ⚡ Quick Start

### Sviluppo Locale

```bash
# Clone del repository
git clone <your-repo-url>
cd qr-code-manager

# Avvio applicazione
./mvnw spring-boot:run
```

**Accesso**: http://localhost:8080
- Admin: `admin@qrmanager.com` / `******`

### Deploy su Render

Segui la guida completa in `README-DEPLOY.md`

## 🔧 Configurazione

### Development
```properties
# application.properties
server.port=8080
spring.datasource.url=jdbc:h2:mem:qrdb
cloudinary.cloud-name=your_cloud_name
```

### Production  
```bash
# Environment Variables
SPRING_PROFILES_ACTIVE=production
CLOUDINARY_CLOUD_NAME=your_cloud_name
CLOUDINARY_API_KEY=your_api_key
CLOUDINARY_API_SECRET=your_api_secret
```

## 🏗️ Architettura

```
├── config/          # Configurazioni Spring
├── controller/      # Controller REST e Thymeleaf  
├── model/           # Entità JPA
├── repository/      # Repository per data access
├── service/         # Business logic
├── util/            # Utilities (QR generation)
└── exception/       # Gestione errori
```

## 🎨 Features

- ✅ Generazione QR code univoci
- ✅ Upload immagini con Cloudinary
- ✅ Dashboard analytics moderna
- ✅ Gestione ruoli admin/user
- ✅ Interface mobile responsive
- ✅ Auto-inizializzazione admin
- ✅ Health checks integrati
- ✅ Deploy zero-config

## 📊 Performance

- **Build Time**: ~2-3 minuti
- **Cold Start**: ~15-20 secondi  
- **Memory Usage**: ~512MB
- **Storage**: Cloudinary (25GB free)

## 🔒 Sicurezza

- Spring Security integrato
- Password crittografate BCrypt
- JWT token per sessioni
- CSRF protection abilitata
- Environment variables per credenziali

## 📈 Monitoring

- Health check: `/actuator/health`
- Metrics: `/actuator/metrics`  
- Logs centralizzati su Render
- Error handling personalizzato

## 🤝 Contributing

1. Fork del repository
2. Crea feature branch (`git checkout -b feature/amazing-feature`)
3. Commit changes (`git commit -m 'Add amazing feature'`)
4. Push branch (`git push origin feature/amazing-feature`)
5. Apri Pull Request

## 📝 License

Questo progetto è sotto licenza MIT - vedi [LICENSE](LICENSE) per dettagli.

## 🙏 Acknowledgments

- Spring Boot community
- Thymeleaf team
- Cloudinary platform
- Render hosting
- ZXing library

---

**Sviluppato con ❤️ per la gestione moderna di QR code**
