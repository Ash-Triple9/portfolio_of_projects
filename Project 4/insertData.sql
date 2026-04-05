-- 1. Populate Membership Tiers (Parent of Member)
INSERT INTO membershipTier VALUES (1, 'day pass', 10.00, 0);
INSERT INTO membershipTier VALUES (2, 'weekly', 50.00, 5);
INSERT INTO membershipTier VALUES (3, 'monthly', 150.00, 10);
INSERT INTO membershipTier VALUES (4, 'annual', 1200.00, 15);
INSERT INTO membershipTier VALUES (5, 'premium', 2000.00, 20);

-- 2. Populate Rooms (Parent of Pet, Event, Reservation)
INSERT INTO room VALUES (101, 'Main Lounge', 'general', 20);
INSERT INTO room VALUES (102, 'Cat Corner', 'cats', 10);
INSERT INTO room VALUES (103, 'Dog Play Area', 'dogs', 15);
INSERT INTO room VALUES (104, 'Quiet Zone', 'small animals', 5);
INSERT INTO room VALUES (105, 'Event Hall', 'multi-purpose', 30);

-- 3. Populate Breeds (Parent of Pet)
INSERT INTO breed VALUES (1, 'Golden Retriever', 'Dog');
INSERT INTO breed VALUES (2, 'Siamese', 'Cat');
INSERT INTO breed VALUES (3, 'Holland Lop', 'Rabbit');
INSERT INTO breed VALUES (4, 'Beagle', 'Dog');
INSERT INTO breed VALUES (5, 'Maine Coon', 'Cat');

-- 4. Populate Food Items (Parent of OrderLine)
INSERT INTO foodItem VALUES (1, 'Latte', 4.50);
INSERT INTO foodItem VALUES (2, 'Green Tea', 3.00);
INSERT INTO foodItem VALUES (3, 'Croissant', 3.50);
INSERT INTO foodItem VALUES (4, 'Club Sandwich', 12.00);
INSERT INTO foodItem VALUES (5, 'Pup Cup', 2.00);

-- 5. Populate Employees (Parent of Application, HealthRecord, etc.)
INSERT INTO employee VALUES (1001, 'Alice', 'Smith', 'Manager');
INSERT INTO employee VALUES (1002, 'Bob', 'Jones', 'Vet Staff');
INSERT INTO employee VALUES (1003, 'Charlie', 'Brown', 'Handler');
INSERT INTO employee VALUES (1004, 'Diana', 'Prince', 'Adoption Coord');
INSERT INTO employee VALUES (1005, 'Evan', 'Wright', 'Barista');

-- 6. Populate Members (Parent of Reservation, Orders, etc.)
INSERT INTO member VALUES (2001, 3, 'John', 'Doe', '520-555-0101', 'john@email.com', TO_TIMESTAMP('1990-05-15', 'YYYY-MM-DD'), 'Jane Doe', '520-555-0102');
INSERT INTO member VALUES (2002, 1, 'Sarah', 'Connor', '520-555-0202', 'sarah@email.com', TO_TIMESTAMP('1985-08-20', 'YYYY-MM-DD'), 'Kyle Reese', '520-555-0203');
INSERT INTO member VALUES (2003, 5, 'Tony', 'Stark', '520-555-0303', 'tony@email.com', TO_TIMESTAMP('1980-02-10', 'YYYY-MM-DD'), 'Pepper Potts', '520-555-0304');
INSERT INTO member VALUES (2004, 2, 'Bruce', 'Wayne', '520-555-0404', 'bruce@email.com', TO_TIMESTAMP('1982-11-01', 'YYYY-MM-DD'), 'Alfred', '520-555-0405');
INSERT INTO member VALUES (2005, 4, 'Clark', 'Kent', '520-555-0505', 'clark@email.com', TO_TIMESTAMP('1988-06-18', 'YYYY-MM-DD'), 'Lois Lane', '520-555-0506');

-- 7. Populate Pets (Parent of Application, HealthRecord)
-- Note: Pet 1 is in a room (available), Pet 5 is deceased, Pet 4 is adopted
INSERT INTO pet VALUES (3001, 103, 1, 'Buddy', 3, TO_TIMESTAMP('2024-01-10', 'YYYY-MM-DD'), 'None', 'Friendly', 'available');
INSERT INTO pet VALUES (3002, 102, 2, 'Luna', 2, TO_TIMESTAMP('2024-02-15', 'YYYY-MM-DD'), 'Sensitive Stomach', 'Shy', 'available');
INSERT INTO pet VALUES (3003, 104, 3, 'Thumper', 1, TO_TIMESTAMP('2024-03-01', 'YYYY-MM-DD'), 'None', 'Energetic', 'in care');
INSERT INTO pet VALUES (3004, NULL, 4, 'Max', 5, TO_TIMESTAMP('2023-11-20', 'YYYY-MM-DD'), 'Arthritis', 'Calm', 'adopted');
INSERT INTO pet VALUES (3005, NULL, 5, 'Shadow', 10, TO_TIMESTAMP('2020-01-01', 'YYYY-MM-DD'), 'None', 'Aloof', 'deceased');

-- 8. Populate Reservations
INSERT INTO reservation VALUES (4001, 2001, 103, TO_TIMESTAMP('2025-11-25 10:00:00', 'YYYY-MM-DD HH24:MI:SS'), TO_TIMESTAMP('2025-11-25 12:00:00', 'YYYY-MM-DD HH24:MI:SS'), NULL, NULL, 'monthly');
INSERT INTO reservation VALUES (4002, 2003, 101, TO_TIMESTAMP('2025-11-26 14:00:00', 'YYYY-MM-DD HH24:MI:SS'), TO_TIMESTAMP('2025-11-26 16:00:00', 'YYYY-MM-DD HH24:MI:SS'), TO_TIMESTAMP('2025-11-26 14:05:00', 'YYYY-MM-DD HH24:MI:SS'), NULL, 'premium');
INSERT INTO reservation VALUES (4003, 2002, 102, TO_TIMESTAMP('2025-12-01 09:00:00', 'YYYY-MM-DD HH24:MI:SS'), TO_TIMESTAMP('2025-12-01 10:00:00', 'YYYY-MM-DD HH24:MI:SS'), NULL, NULL, 'day pass');

-- 9. Populate Events
INSERT INTO event VALUES (5001, 105, 1001, 'Puppy Yoga', TO_TIMESTAMP('2025-12-10', 'YYYY-MM-DD'), TO_TIMESTAMP('2025-12-10 09:00:00', 'YYYY-MM-DD HH24:MI:SS'), 20);
INSERT INTO event VALUES (5002, 105, 1002, 'Pet Care 101', TO_TIMESTAMP('2025-12-15', 'YYYY-MM-DD'), TO_TIMESTAMP('2025-12-15 18:00:00', 'YYYY-MM-DD HH24:MI:SS'), 30);

-- 10. Populate Event Bookings
INSERT INTO eventBooking VALUES (6001, 2001, 5001, TO_TIMESTAMP('2025-11-20', 'YYYY-MM-DD'), 'registered', 'paid');
INSERT INTO eventBooking VALUES (6002, 2004, 5001, TO_TIMESTAMP('2025-11-21', 'YYYY-MM-DD'), 'canceled', 'unpaid');

-- 11. Populate Applications (Parent of Adoption)
INSERT INTO application VALUES (7001, 2004, 3004, 1004, TO_TIMESTAMP('2024-05-01', 'YYYY-MM-DD'), 'adopted');
INSERT INTO application VALUES (7002, 2001, 3001, 1004, TO_TIMESTAMP('2025-11-15', 'YYYY-MM-DD'), 'pending');
INSERT INTO application VALUES (7003, 2005, 3002, 1004, TO_TIMESTAMP('2025-11-18', 'YYYY-MM-DD'), 'rejected');

-- 12. Populate Adoptions (Links Application 7001)
INSERT INTO adoption VALUES (8001, 7001, TO_TIMESTAMP('2024-05-10', 'YYYY-MM-DD'), 150.00, TO_TIMESTAMP('2024-06-10', 'YYYY-MM-DD'));

-- 13. Populate Health Records
INSERT INTO healthRecord VALUES (9001, 3001, 1002, TO_TIMESTAMP('2025-10-01', 'YYYY-MM-DD'), 'Vaccination', 'Rabies booster', NULL, TO_TIMESTAMP('2026-10-01', 'YYYY-MM-DD'), 'active');
INSERT INTO healthRecord VALUES (9002, 3003, 1002, TO_TIMESTAMP('2025-11-01', 'YYYY-MM-DD'), 'Checkup', 'General health check', NULL, TO_TIMESTAMP('2026-05-01', 'YYYY-MM-DD'), 'active');
INSERT INTO healthRecord VALUES (9003, 3001, 1002, TO_TIMESTAMP('2025-09-01', 'YYYY-MM-DD'), 'Error Entry', 'Mistake record', 'Entered wrong pet ID', NULL, 'void');

-- 14. Populate Orders (Parent of OrderLine)
-- Order 1: Connected to Reservation 4002, Employee 1005 (Barista)
INSERT INTO orders VALUES (10001, 2003, 1005, 4002, TO_TIMESTAMP('2025-11-26 14:30:00', 'YYYY-MM-DD HH24:MI:SS'), 16.50, 'complete');
-- Order 2: Walk-in (No Reservation), Employee 1005
INSERT INTO orders VALUES (10002, 2001, 1005, NULL, TO_TIMESTAMP('2025-11-27 10:00:00', 'YYYY-MM-DD HH24:MI:SS'), 4.50, 'pending');

-- 15. Populate OrderLines
-- Order 10001: 1 Club Sandwich (12.00) + 1 Latte (4.50) = 16.50
INSERT INTO orderLine VALUES (10001, 4, 1, 12.00);
INSERT INTO orderLine VALUES (10001, 1, 1, 4.50);
-- Order 10002: 1 Latte (4.50)
INSERT INTO orderLine VALUES (10002, 1, 1, 4.50);