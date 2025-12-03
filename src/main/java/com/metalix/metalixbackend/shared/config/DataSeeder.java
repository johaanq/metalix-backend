package com.metalix.metalixbackend.shared.config;

import com.metalix.metalixbackend.iam.domain.model.aggregates.User;
import com.metalix.metalixbackend.iam.domain.model.valueobjects.Role;
import com.metalix.metalixbackend.iam.domain.repository.UserRepository;
import com.metalix.metalixbackend.monitoring.domain.model.aggregates.Report;
import com.metalix.metalixbackend.monitoring.domain.model.entities.Alert;
import com.metalix.metalixbackend.monitoring.domain.model.entities.Metric;
import com.metalix.metalixbackend.monitoring.domain.model.valueobjects.AlertSeverity;
import com.metalix.metalixbackend.monitoring.domain.model.valueobjects.AlertType;
import com.metalix.metalixbackend.monitoring.domain.model.valueobjects.MetricCategory;
import com.metalix.metalixbackend.monitoring.domain.model.valueobjects.ReportStatus;
import com.metalix.metalixbackend.monitoring.domain.model.valueobjects.ReportType;
import com.metalix.metalixbackend.monitoring.domain.repository.AlertRepository;
import com.metalix.metalixbackend.monitoring.domain.repository.MetricRepository;
import com.metalix.metalixbackend.monitoring.domain.repository.ReportRepository;
import com.metalix.metalixbackend.municipality.domain.model.aggregates.Municipality;
import com.metalix.metalixbackend.municipality.domain.model.entities.Zone;
import com.metalix.metalixbackend.municipality.domain.model.valueobjects.ZoneType;
import com.metalix.metalixbackend.municipality.domain.repository.MunicipalityRepository;
import com.metalix.metalixbackend.municipality.domain.repository.ZoneRepository;
import com.metalix.metalixbackend.reward.domain.model.aggregates.Reward;
import com.metalix.metalixbackend.reward.domain.model.entities.RewardTransaction;
import com.metalix.metalixbackend.reward.domain.model.valueobjects.RewardCategory;
import com.metalix.metalixbackend.reward.domain.model.valueobjects.TransactionStatus;
import com.metalix.metalixbackend.reward.domain.model.valueobjects.TransactionType;
import com.metalix.metalixbackend.reward.domain.repository.RewardRepository;
import com.metalix.metalixbackend.reward.domain.repository.RewardTransactionRepository;
import com.metalix.metalixbackend.useridentification.domain.model.aggregates.RfidCard;
import com.metalix.metalixbackend.useridentification.domain.model.valueobjects.CardStatus;
import com.metalix.metalixbackend.useridentification.domain.repository.RfidCardRepository;
import com.metalix.metalixbackend.wastecollection.domain.model.aggregates.WasteCollector;
import com.metalix.metalixbackend.wastecollection.domain.model.entities.SensorData;
import com.metalix.metalixbackend.wastecollection.domain.model.entities.WasteCollection;
import com.metalix.metalixbackend.wastecollection.domain.model.valueobjects.CollectorStatus;
import com.metalix.metalixbackend.wastecollection.domain.model.valueobjects.CollectorType;
import com.metalix.metalixbackend.wastecollection.domain.model.valueobjects.RecyclableType;
import com.metalix.metalixbackend.wastecollection.domain.model.valueobjects.SensorStatus;
import com.metalix.metalixbackend.wastecollection.domain.model.valueobjects.VerificationMethod;
import com.metalix.metalixbackend.wastecollection.domain.repository.SensorDataRepository;
import com.metalix.metalixbackend.wastecollection.domain.repository.WasteCollectorRepository;
import com.metalix.metalixbackend.wastecollection.domain.repository.WasteCollectionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DataSeeder {
    
    private volatile boolean seeded = false;

    private final UserRepository userRepository;
    private final MunicipalityRepository municipalityRepository;
    private final ZoneRepository zoneRepository;
    private final RfidCardRepository rfidCardRepository;
    private final WasteCollectorRepository wasteCollectorRepository;
    private final WasteCollectionRepository wasteCollectionRepository;
    private final SensorDataRepository sensorDataRepository;
    private final RewardRepository rewardRepository;
    private final RewardTransactionRepository rewardTransactionRepository;
    private final ReportRepository reportRepository;
    private final MetricRepository metricRepository;
    private final AlertRepository alertRepository;
    private final PasswordEncoder passwordEncoder;

    private final Random random = new Random();
    private final String DEFAULT_PASSWORD = "password123";

    @EventListener(ApplicationReadyEvent.class)
    public void seedData() {
        if (seeded) {
            log.info("Data already seeded, skipping...");
            return;
        }
        
        try {
            long userCount = userRepository.count();
            long municipalityCount = municipalityRepository.count();
            log.info("Current user count: {}, municipality count: {}", userCount, municipalityCount);
            
            log.info("Starting data seeding...");

            // 1. Create Municipalities
            List<Municipality> municipalities = createMunicipalities();
            log.info("Created {} municipalities", municipalities.size());

            // 2. Create Zones
            List<Zone> zones = createZones(municipalities);
            log.info("Created {} zones", zones.size());

            // 3. Create Users
            List<User> users = createUsers(municipalities);
            log.info("Created {} users", users.size());

            // 4. Create RFID Cards
            List<RfidCard> rfidCards = createRfidCards(users);
            log.info("Created {} RFID cards", rfidCards.size());

            // 5. Create Waste Collectors
            List<WasteCollector> collectors = createWasteCollectors(municipalities, zones);
            log.info("Created {} waste collectors", collectors.size());

            // 6. Create Waste Collections
            List<WasteCollection> collections = createWasteCollections(users, collectors, municipalities, zones);
            log.info("Created {} waste collections", collections.size());

            // 7. Create Sensor Data
            List<SensorData> sensorData = createSensorData(collectors);
            log.info("Created {} sensor data entries", sensorData.size());

            // 8. Create Rewards
            List<Reward> rewards = createRewards(municipalities);
            log.info("Created {} rewards", rewards.size());

            // 9. Create Reward Transactions
            List<RewardTransaction> transactions = createRewardTransactions(users, rewards);
            log.info("Created {} reward transactions", transactions.size());

            // 10. Create Reports
            List<Report> reports = createReports(municipalities, users);
            log.info("Created {} reports", reports.size());

            // 11. Create Metrics
            List<Metric> metrics = createMetrics(municipalities);
            log.info("Created {} metrics", metrics.size());

            // 12. Create Alerts
            List<Alert> alerts = createAlerts(municipalities, collectors);
            log.info("Created {} alerts", alerts.size());

            log.info("Data seeding completed successfully!");
            seeded = true;
        } catch (Exception e) {
            log.error("Error during data seeding: ", e);
            e.printStackTrace();
        }
    }

    private List<Municipality> createMunicipalities() {
        List<Municipality> municipalities = new ArrayList<>();
        List<Municipality> existingMunicipalities = municipalityRepository.findAll();
        
        // Verificar si ya existen municipalidades con estos códigos
        String[] codes = {"LIMA001", "AREQ001", "CUSC001", "TRUJ001", "CHIC001"};
        boolean hasExisting = existingMunicipalities.stream()
                .anyMatch(m -> java.util.Arrays.asList(codes).contains(m.getCode()));
        
        if (hasExisting) {
            log.info("Municipalities already exist, skipping creation. Using existing municipalities.");
            return existingMunicipalities;
        }

        municipalities.add(createMunicipality("Municipalidad de Lima", "LIMA001", "Lima", 9000000, 2672.0, "contacto@munilima.gob.pe", "+51-1-315-2000"));
        municipalities.add(createMunicipality("Municipalidad de Arequipa", "AREQ001", "Arequipa", 1000000, 633.0, "contacto@muniarequipa.gob.pe", "+51-54-221-0000"));
        municipalities.add(createMunicipality("Municipalidad de Cusco", "CUSC001", "Cusco", 500000, 385.0, "contacto@municusco.gob.pe", "+51-84-223-0000"));
        municipalities.add(createMunicipality("Municipalidad de Trujillo", "TRUJ001", "La Libertad", 800000, 1768.0, "contacto@munitrujillo.gob.pe", "+51-44-231-0000"));
        municipalities.add(createMunicipality("Municipalidad de Chiclayo", "CHIC001", "Lambayeque", 600000, 621.0, "contacto@municiclayo.gob.pe", "+51-74-223-0000"));

        try {
            return municipalityRepository.saveAll(municipalities);
        } catch (Exception e) {
            log.warn("Error creating municipalities, using existing ones: {}", e.getMessage());
            return municipalityRepository.findAll();
        }
    }

    private Municipality createMunicipality(String name, String code, String region, Integer population, Double area, String email, String phone) {
        Municipality municipality = new Municipality();
        municipality.setName(name);
        municipality.setCode(code);
        municipality.setRegion(region);
        municipality.setPopulation(population);
        municipality.setArea(area);
        municipality.setContactEmail(email);
        municipality.setContactPhone(phone);
        municipality.setIsActive(true);
        return municipality;
    }

    private List<Zone> createZones(List<Municipality> municipalities) {
        List<Zone> zonesToCreate = new ArrayList<>();
        
        // Obtener todas las zonas existentes
        List<Zone> existingZones = zoneRepository.findAll();

        for (Municipality municipality : municipalities) {
            String[] zoneNames = {"Centro Histórico", "Zona Norte", "Zona Sur", "Distrito Industrial", "Zona Rural"};
            ZoneType[] zoneTypes = {ZoneType.RESIDENTIAL, ZoneType.RESIDENTIAL, ZoneType.COMMERCIAL, ZoneType.INDUSTRIAL, ZoneType.RURAL};
            String[] schedules = {
                "Lunes, Miércoles, Viernes 6:00 AM",
                "Martes, Jueves, Sábado 7:00 AM",
                "Lunes a Viernes 8:00 AM",
                "Lunes, Miércoles, Viernes 9:00 AM",
                "Lunes y Jueves 6:00 AM"
            };

            for (int i = 0; i < zoneNames.length; i++) {
                String zoneName = zoneNames[i];
                // Verificar si ya existe una zona con este nombre para esta municipalidad
                boolean zoneExists = existingZones.stream()
                    .anyMatch(z -> z.getName().equals(zoneName) && 
                                  z.getMunicipalityId().equals(municipality.getId()));
                
                if (!zoneExists) {
                    zonesToCreate.add(createZone(zoneName, municipality.getId(), zoneTypes[i], schedules[i]));
                }
            }
        }

        if (zonesToCreate.isEmpty()) {
            log.info("All zones already exist, returning existing zones.");
            return zoneRepository.findAll();
        }

        try {
            log.info("Creating {} new zones...", zonesToCreate.size());
            List<Zone> savedZones = zoneRepository.saveAll(zonesToCreate);
            log.info("Successfully created {} zones", savedZones.size());
            return zoneRepository.findAll(); // Retornar todas las zonas (nuevas + existentes)
        } catch (Exception e) {
            log.error("Error creating zones: {}", e.getMessage(), e);
            // Si hay error, retornar las existentes en lugar de fallar
            return zoneRepository.findAll();
        }
    }

    private Zone createZone(String name, Long municipalityId, ZoneType type, String schedule) {
        Zone zone = new Zone();
        zone.setName(name);
        zone.setMunicipalityId(municipalityId);
        zone.setType(type);
        zone.setCollectionSchedule(schedule);
        zone.setDescription("Zona de recolección de residuos: " + name);
        zone.setIsActive(true);
        return zone;
    }

    private List<User> createUsers(List<Municipality> municipalities) {
        List<User> usersToCreate = new ArrayList<>();
        long existingUserCount = userRepository.count();
        
        log.info("Checking existing users. Current count: {}", existingUserCount);

        // System Admin
        String systemAdminEmail = "admin@metalix.com";
        if (!userRepository.existsByEmail(systemAdminEmail)) {
            User systemAdmin = new User();
            systemAdmin.setEmail(systemAdminEmail);
            systemAdmin.setPassword(passwordEncoder.encode(DEFAULT_PASSWORD));
            systemAdmin.setFirstName("Sistema");
            systemAdmin.setLastName("Administrador");
            systemAdmin.setRole(Role.SYSTEM_ADMIN);
            systemAdmin.setPhone("+51-999-999-999");
            systemAdmin.setAddress("Oficina Central Metalix");
            systemAdmin.setCity("Lima");
            systemAdmin.setZipCode("15001");
            systemAdmin.setIsActive(true);
            systemAdmin.setTotalPoints(0);
            usersToCreate.add(systemAdmin);
            log.debug("Adding system admin user: {}", systemAdminEmail);
        } else {
            log.debug("System admin user already exists: {}", systemAdminEmail);
        }

        // Municipality Admins
        for (int i = 0; i < municipalities.size(); i++) {
            Municipality muni = municipalities.get(i);
            String adminEmail = "admin." + muni.getCode().toLowerCase() + "@metalix.com";
            if (!userRepository.existsByEmail(adminEmail)) {
                User admin = new User();
                admin.setEmail(adminEmail);
                admin.setPassword(passwordEncoder.encode(DEFAULT_PASSWORD));
                admin.setFirstName("Administrador");
                admin.setLastName(muni.getName());
                admin.setRole(Role.MUNICIPALITY_ADMIN);
                admin.setMunicipalityId(muni.getId());
                admin.setPhone(muni.getContactPhone());
                admin.setAddress("Municipalidad de " + muni.getName());
                admin.setCity(muni.getRegion());
                admin.setIsActive(true);
                admin.setTotalPoints(0);
                usersToCreate.add(admin);
                log.debug("Adding municipality admin user: {}", adminEmail);
            } else {
                log.debug("Municipality admin user already exists: {}", adminEmail);
            }
        }

        // Citizens - generar emails únicos usando el índice
        String[] firstNames = {"María", "José", "Ana", "Carlos", "Laura", "Pedro", "Carmen", "Luis", "Sofía", "Miguel"};
        String[] lastNames = {"López", "Pérez", "García", "Rodríguez", "Martínez", "González", "Fernández", "Torres", "Ramírez", "Sánchez"};

        for (int i = 0; i < 20; i++) {
            // Generar email único usando el índice para evitar duplicados
            String citizenEmail = firstNames[i % firstNames.length].toLowerCase() + "." + 
                                 lastNames[i % lastNames.length].toLowerCase() + i + "@email.com";
            
            if (!userRepository.existsByEmail(citizenEmail)) {
                User citizen = new User();
                citizen.setEmail(citizenEmail);
                citizen.setPassword(passwordEncoder.encode(DEFAULT_PASSWORD));
                citizen.setFirstName(firstNames[i % firstNames.length]);
                citizen.setLastName(lastNames[i % lastNames.length]);
                citizen.setRole(Role.CITIZEN);
                citizen.setMunicipalityId(municipalities.get(i % municipalities.size()).getId());
                citizen.setPhone("+51-9" + String.format("%08d", 10000000 + i));
                citizen.setAddress("Calle " + (i + 1) + " #" + (i * 10));
                citizen.setCity(municipalities.get(i % municipalities.size()).getRegion());
                citizen.setZipCode(String.format("%05d", 15000 + i));
                citizen.setIsActive(true);
                citizen.setTotalPoints(random.nextInt(500)); // 0-500 puntos iniciales
                usersToCreate.add(citizen);
            }
        }

        log.info("Users to create: {}", usersToCreate.size());

        if (usersToCreate.isEmpty()) {
            log.info("All users already exist, returning existing users.");
            List<User> existingUsers = userRepository.findAll();
            log.info("Returning {} existing users", existingUsers.size());
            return existingUsers;
        }

        try {
            log.info("Saving {} new users...", usersToCreate.size());
            List<User> savedUsers = userRepository.saveAll(usersToCreate);
            log.info("Successfully saved {} users", savedUsers.size());
            
            // Obtener todos los usuarios (nuevos + existentes)
            List<User> allUsers = userRepository.findAll();
            log.info("Total users in database: {}", allUsers.size());
            return allUsers;
        } catch (Exception e) {
            log.error("Error creating users: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create users: " + e.getMessage(), e);
        }
    }

    private List<RfidCard> createRfidCards(List<User> users) {
        List<RfidCard> cardsToCreate = new ArrayList<>();
        List<User> usersToUpdate = new ArrayList<>();

        // Asignar tarjetas RFID a ciudadanos (primeros 15)
        List<User> citizens = users.stream()
                .filter(u -> u.getRole() == Role.CITIZEN)
                .limit(15)
                .toList();

        log.info("Checking RFID cards for {} citizens", citizens.size());

        for (int i = 0; i < citizens.size(); i++) {
            User user = citizens.get(i);
            String cardNumber = "RFID" + String.format("%08d", 10000000 + i);
            
            // Verificar si el usuario ya tiene una tarjeta asignada
            if (rfidCardRepository.findByUserId(user.getId()).isPresent()) {
                log.debug("User {} already has an RFID card assigned", user.getId());
                continue;
            }
            
            // Verificar si la tarjeta ya existe
            if (rfidCardRepository.existsByCardNumber(cardNumber)) {
                log.debug("RFID card {} already exists", cardNumber);
                // Si la tarjeta existe pero no está asignada a este usuario, asignarla
                RfidCard existingCard = rfidCardRepository.findByCardNumber(cardNumber).orElse(null);
                if (existingCard != null && existingCard.getUserId() == null) {
                    existingCard.setUserId(user.getId());
                    rfidCardRepository.save(existingCard);
                    user.setRfidCard(cardNumber);
                    usersToUpdate.add(user);
                }
                continue;
            }
            
            // Crear nueva tarjeta
            RfidCard card = new RfidCard();
            card.setCardNumber(cardNumber);
            card.setUserId(user.getId());
            card.setStatus(CardStatus.ACTIVE);
            card.setIssuedDate(LocalDate.now().minusDays(random.nextInt(365)));
            card.setExpirationDate(LocalDate.now().plusYears(2));
            card.setLastUsed(LocalDateTime.now().minusDays(random.nextInt(30)));
            cardsToCreate.add(card);

            // Actualizar el usuario con el número de tarjeta
            user.setRfidCard(cardNumber);
            usersToUpdate.add(user);
        }

        log.info("RFID cards to create: {}", cardsToCreate.size());

        // Guardar usuarios actualizados
        if (!usersToUpdate.isEmpty()) {
            userRepository.saveAll(usersToUpdate);
        }

        if (cardsToCreate.isEmpty()) {
            log.info("All RFID cards already exist, returning existing cards.");
            return rfidCardRepository.findAll();
        }

        try {
            List<RfidCard> savedCards = rfidCardRepository.saveAll(cardsToCreate);
            log.info("Successfully created {} RFID cards", savedCards.size());
            return rfidCardRepository.findAll(); // Retornar todas las tarjetas (nuevas + existentes)
        } catch (Exception e) {
            log.error("Error creating RFID cards: {}", e.getMessage(), e);
            // Si hay error, retornar las existentes en lugar de fallar
            return rfidCardRepository.findAll();
        }
    }

    private List<WasteCollector> createWasteCollectors(List<Municipality> municipalities, List<Zone> zones) {
        List<WasteCollector> collectors = new ArrayList<>();
        CollectorType[] types = CollectorType.values();

        for (Municipality municipality : municipalities) {
            List<Zone> municipalityZones = zones.stream()
                    .filter(z -> z.getMunicipalityId().equals(municipality.getId()))
                    .toList();

            // Crear 3-5 contenedores por zona
            for (Zone zone : municipalityZones) {
                int collectorsPerZone = 3 + random.nextInt(3); // 3-5 contenedores

                for (int i = 0; i < collectorsPerZone; i++) {
                    WasteCollector collector = new WasteCollector();
                    collector.setName("Contenedor " + zone.getName() + " #" + (i + 1));
                    collector.setType(types[random.nextInt(types.length)]);
                    collector.setLocation("Ubicación " + (i + 1) + " - " + zone.getName());
                    collector.setMunicipalityId(municipality.getId());
                    collector.setZoneId(zone.getId());
                    collector.setCapacity(100.0 + random.nextDouble() * 400.0); // 100-500 kg
                    collector.setCurrentFill(random.nextDouble() * collector.getCapacity() * 0.8); // 0-80% lleno
                    collector.setLastCollection(LocalDateTime.now().minusDays(random.nextInt(7)));
                    collector.setNextScheduledCollection(LocalDateTime.now().plusDays(1 + random.nextInt(3)));
                    
                    // Establecer estado basado en nivel de llenado
                    if (collector.getFillPercentage() > 80) {
                        collector.setStatus(CollectorStatus.FULL);
                    } else if (collector.getFillPercentage() > 60) {
                        collector.setStatus(CollectorStatus.ACTIVE);
                    } else {
                        collector.setStatus(CollectorStatus.ACTIVE);
                    }
                    
                    collector.setSensorId("SENSOR-" + municipality.getCode() + "-" + zone.getId() + "-" + (i + 1));
                    collectors.add(collector);
                }
            }
        }

        return wasteCollectorRepository.saveAll(collectors);
    }

    private List<WasteCollection> createWasteCollections(List<User> users, List<WasteCollector> collectors, 
                                                          List<Municipality> municipalities, List<Zone> zones) {
        List<WasteCollection> collections = new ArrayList<>();
        List<User> citizens = users.stream().filter(u -> u.getRole() == Role.CITIZEN).toList();
        RecyclableType[] recyclableTypes = RecyclableType.values();
        VerificationMethod[] verificationMethods = VerificationMethod.values();

        // Crear 50-100 colecciones
        int numCollections = 50 + random.nextInt(51);

        for (int i = 0; i < numCollections; i++) {
            User user = citizens.get(random.nextInt(citizens.size()));
            WasteCollector collector = collectors.get(random.nextInt(collectors.size()));
            RecyclableType recyclableType = recyclableTypes[random.nextInt(recyclableTypes.length)];

            WasteCollection collection = new WasteCollection();
            collection.setUserId(user.getId());
            collection.setCollectorId(collector.getId());
            collection.setWeight(0.5 + random.nextDouble() * 9.5); // 0.5-10 kg
            collection.setTimestamp(LocalDateTime.now().minusDays(random.nextInt(90))); // Últimos 90 días
            collection.setRecyclableType(recyclableType);
            collection.setVerified(random.nextBoolean());
            collection.setVerificationMethod(verificationMethods[random.nextInt(verificationMethods.length)]);
            collection.setMunicipalityId(collector.getMunicipalityId());
            collection.setZoneId(collector.getZoneId());
            
            // Calcular puntos
            collection.calculatePoints();
            
            // Actualizar puntos del usuario
            user.addPoints(collection.getPoints());
            
            collections.add(collection);
        }

        userRepository.saveAll(citizens);
        return wasteCollectionRepository.saveAll(collections);
    }

    private List<SensorData> createSensorData(List<WasteCollector> collectors) {
        List<SensorData> sensorDataList = new ArrayList<>();

        // Crear datos de sensores para cada contenedor (últimos 30 días)
        for (WasteCollector collector : collectors) {
            if (collector.getSensorId() != null) {
                // Crear 5-10 lecturas por contenedor
                int readings = 5 + random.nextInt(6);
                
                for (int i = 0; i < readings; i++) {
                    SensorData data = new SensorData();
                    data.setSensorId(collector.getSensorId());
                    data.setCollectorId(collector.getId());
                    data.setFillLevel(collector.getCurrentFill() + (random.nextDouble() - 0.5) * 10);
                    data.setTemperature(15.0 + random.nextDouble() * 20.0); // 15-35°C
                    data.setBatteryLevel(20 + random.nextInt(81)); // 20-100%
                    data.setTimestamp(LocalDateTime.now().minusDays(random.nextInt(30)));
                    data.setStatus(SensorStatus.ACTIVE);
                    sensorDataList.add(data);
                }
            }
        }

        return sensorDataRepository.saveAll(sensorDataList);
    }

    private List<Reward> createRewards(List<Municipality> municipalities) {
        List<Reward> rewards = new ArrayList<>();

        // Recompensas globales (sin municipalidad específica)
        rewards.add(createReward("Descuento 10% en Supermercado", "Descuento del 10% en compras", 100, RewardCategory.DISCOUNT, null, null));
        rewards.add(createReward("Cupón de Cine", "2 entradas de cine gratis", 200, RewardCategory.VOUCHER, null, null));
        rewards.add(createReward("Producto Ecológico", "Kit de productos ecológicos", 150, RewardCategory.PRODUCT, null, null));
        rewards.add(createReward("Experiencia de Reciclaje", "Tour guiado a planta de reciclaje", 300, RewardCategory.EXPERIENCE, null, null));
        rewards.add(createReward("Donación a Caridad", "Donación equivalente a ONG ambiental", 250, RewardCategory.CHARITY, null, null));

        // Recompensas por municipalidad
        for (Municipality municipality : municipalities) {
            rewards.add(createReward("Descuento Municipal " + municipality.getName(), "Descuento en servicios municipales", 120, RewardCategory.DISCOUNT, municipality.getId(), null));
            rewards.add(createReward("Producto Local " + municipality.getName(), "Producto de comercio local", 180, RewardCategory.PRODUCT, municipality.getId(), null));
        }

        return rewardRepository.saveAll(rewards);
    }

    private Reward createReward(String name, String description, Integer pointsCost, RewardCategory category, Long municipalityId, LocalDate expirationDate) {
        Reward reward = new Reward();
        reward.setName(name);
        reward.setDescription(description);
        reward.setPointsCost(pointsCost);
        reward.setCategory(category);
        reward.setAvailability(10 + random.nextInt(91)); // 10-100 disponibles
        reward.setMunicipalityId(municipalityId);
        reward.setImageUrl("https://example.com/rewards/" + name.toLowerCase().replace(" ", "-") + ".jpg");
        reward.setExpirationDate(expirationDate != null ? expirationDate : LocalDate.now().plusMonths(6));
        reward.setTermsAndConditions("Términos y condiciones aplicables");
        reward.setIsActive(true);
        return reward;
    }

    private List<RewardTransaction> createRewardTransactions(List<User> users, List<Reward> rewards) {
        List<RewardTransaction> transactions = new ArrayList<>();
        List<User> citizens = users.stream().filter(u -> u.getRole() == Role.CITIZEN).toList();

        // Transacciones de ganancia de puntos (de colecciones)
        for (User user : citizens) {
            int earningTransactions = random.nextInt(5) + 1; // 1-5 transacciones
            
            for (int i = 0; i < earningTransactions; i++) {
                RewardTransaction transaction = new RewardTransaction();
                transaction.setUserId(user.getId());
                transaction.setRewardId(null);
                transaction.setTransactionType(TransactionType.EARN);
                transaction.setPoints(10 + random.nextInt(100)); // 10-110 puntos
                transaction.setDescription("Puntos ganados por recolección de residuos");
                transaction.setTimestamp(LocalDateTime.now().minusDays(random.nextInt(60)));
                transaction.setStatus(TransactionStatus.COMPLETED);
                transactions.add(transaction);
            }
        }

        // Transacciones de canje (algunos usuarios canjean recompensas)
        for (int i = 0; i < 10; i++) {
            User user = citizens.get(random.nextInt(citizens.size()));
            Reward reward = rewards.get(random.nextInt(rewards.size()));
            
            if (user.getTotalPoints() >= reward.getPointsCost()) {
                RewardTransaction transaction = new RewardTransaction();
                transaction.setUserId(user.getId());
                transaction.setRewardId(reward.getId());
                transaction.setTransactionType(TransactionType.REDEEM);
                transaction.setPoints(reward.getPointsCost());
                transaction.setDescription("Canje de recompensa: " + reward.getName());
                transaction.setTimestamp(LocalDateTime.now().minusDays(random.nextInt(30)));
                transaction.setStatus(TransactionStatus.COMPLETED);
                transactions.add(transaction);
                
                // Actualizar puntos del usuario
                user.deductPoints(reward.getPointsCost());
            }
        }

        userRepository.saveAll(citizens);
        return rewardTransactionRepository.saveAll(transactions);
    }

    private List<Report> createReports(List<Municipality> municipalities, List<User> users) {
        List<Report> reports = new ArrayList<>();
        ReportType[] reportTypes = ReportType.values();
        List<User> admins = users.stream()
                .filter(u -> u.getRole() == Role.SYSTEM_ADMIN || u.getRole() == Role.MUNICIPALITY_ADMIN)
                .toList();

        // Reportes por municipalidad
        for (Municipality municipality : municipalities) {
            for (int i = 0; i < 3; i++) {
                Report report = new Report();
                report.setTitle("Reporte " + reportTypes[random.nextInt(reportTypes.length)] + " - " + municipality.getName());
                report.setType(reportTypes[random.nextInt(reportTypes.length)]);
                report.setGeneratedBy(admins.get(random.nextInt(admins.size())).getId());
                report.setGeneratedAt(LocalDateTime.now().minusDays(random.nextInt(30)));
                report.setMunicipalityId(municipality.getId());
                report.setDateRange("Último mes");
                report.setSummary("Resumen del reporte de " + municipality.getName());
                report.setMetrics("{\"collections\": " + (100 + random.nextInt(900)) + ", \"recycled\": " + (50 + random.nextInt(450)) + "}");
                report.setStatus(ReportStatus.GENERATED);
                reports.add(report);
            }
        }

        // Reportes globales
        for (int i = 0; i < 5; i++) {
            Report report = new Report();
            report.setTitle("Reporte Global " + reportTypes[random.nextInt(reportTypes.length)]);
            report.setType(reportTypes[random.nextInt(reportTypes.length)]);
            report.setGeneratedBy(admins.stream()
                    .filter(u -> u.getRole() == Role.SYSTEM_ADMIN)
                    .findFirst()
                    .orElse(admins.get(0))
                    .getId());
            report.setGeneratedAt(LocalDateTime.now().minusDays(random.nextInt(30)));
            report.setMunicipalityId(null);
            report.setDateRange("Último trimestre");
            report.setSummary("Reporte global del sistema");
            report.setMetrics("{\"totalCollections\": " + (1000 + random.nextInt(9000)) + ", \"totalRecycled\": " + (500 + random.nextInt(4500)) + "}");
            report.setStatus(ReportStatus.GENERATED);
            reports.add(report);
        }

        return reportRepository.saveAll(reports);
    }

    private List<Metric> createMetrics(List<Municipality> municipalities) {
        List<Metric> metrics = new ArrayList<>();
        MetricCategory[] categories = MetricCategory.values();

        // Métricas por municipalidad
        for (Municipality municipality : municipalities) {
            for (MetricCategory category : categories) {
                Metric metric = new Metric();
                metric.setName("Métrica " + category + " - " + municipality.getName());
                metric.setValue(100.0 + random.nextDouble() * 900.0);
                metric.setUnit("kg");
                metric.setCategory(category);
                metric.setMunicipalityId(municipality.getId());
                metric.setTimestamp(LocalDateTime.now().minusDays(random.nextInt(7)));
                metric.setTrend(random.nextBoolean() ? "up" : "down");
                metrics.add(metric);
            }
        }

        // Métricas globales
        for (MetricCategory category : categories) {
            Metric metric = new Metric();
            metric.setName("Métrica Global " + category);
            metric.setValue(1000.0 + random.nextDouble() * 9000.0);
            metric.setUnit("kg");
            metric.setCategory(category);
            metric.setMunicipalityId(null);
            metric.setTimestamp(LocalDateTime.now().minusDays(random.nextInt(7)));
            metric.setTrend(random.nextBoolean() ? "up" : "down");
            metrics.add(metric);
        }

        return metricRepository.saveAll(metrics);
    }

    private List<Alert> createAlerts(List<Municipality> municipalities, List<WasteCollector> collectors) {
        List<Alert> alerts = new ArrayList<>();
        AlertType[] alertTypes = AlertType.values();
        AlertSeverity[] severities = AlertSeverity.values();

        // Alertas de contenedores llenos
        List<WasteCollector> fullCollectors = collectors.stream()
                .filter(c -> c.getStatus() == CollectorStatus.FULL)
                .limit(10)
                .toList();

        for (WasteCollector collector : fullCollectors) {
            Alert alert = new Alert();
            alert.setTitle("Contenedor Lleno: " + collector.getName());
            alert.setMessage("El contenedor " + collector.getName() + " está al " + String.format("%.1f", collector.getFillPercentage()) + "% de su capacidad");
            alert.setType(AlertType.COLLECTOR_FULL);
            alert.setSeverity(AlertSeverity.MEDIUM);
            alert.setSource("Sensor " + collector.getSensorId());
            alert.setMunicipalityId(collector.getMunicipalityId());
            alert.setTimestamp(LocalDateTime.now().minusHours(random.nextInt(48)));
            alert.setIsRead(random.nextBoolean());
            alert.setIsResolved(false);
            alert.setActionRequired(true);
            alerts.add(alert);
        }

        // Otras alertas
        for (int i = 0; i < 15; i++) {
            Municipality municipality = municipalities.get(random.nextInt(municipalities.size()));
            Alert alert = new Alert();
            alert.setTitle("Alerta: " + alertTypes[random.nextInt(alertTypes.length)]);
            alert.setMessage("Descripción de la alerta generada automáticamente");
            alert.setType(alertTypes[random.nextInt(alertTypes.length)]);
            alert.setSeverity(severities[random.nextInt(severities.length)]);
            alert.setSource("Sistema");
            alert.setMunicipalityId(municipality.getId());
            alert.setTimestamp(LocalDateTime.now().minusDays(random.nextInt(7)));
            alert.setIsRead(random.nextBoolean());
            alert.setIsResolved(random.nextBoolean());
            alert.setActionRequired(random.nextBoolean());
            if (alert.getIsResolved()) {
                alert.setResolvedAt(alert.getTimestamp().plusHours(random.nextInt(24)));
            }
            alerts.add(alert);
        }

        return alertRepository.saveAll(alerts);
    }
}

