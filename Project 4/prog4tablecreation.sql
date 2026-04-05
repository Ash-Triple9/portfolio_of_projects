--1
create table room (
    roomID integer primary key not null,
    roomName varchar2(50) not null,
    roomType varchar2(50) not null,
    maxCapacity integer not null
);
grant insert, update, delete, select on room to public;

--2
create table breed (
    breedID integer primary key not null,
    breedName varchar2(50),
    species varchar2(50)
);
grant insert, update, delete, select on breed to public;

--3
create table membershipTier (
    tierID integer primary key not null,
    name varchar2(50) not null,
    price number(10,2) not null,
    discountRate integer not null, -- Lowest is 0% discount but cannot be null
    constraint chkTierName check (name in ('day pass', 'weekly', 'monthly', 'annual', 'premium')) -- Brainstorm tier names
);
grant insert, update, delete, select on membershipTier to public;

--4
create table foodItem (
    itemID integer primary key not null,
    itemName varchar2(50) not null,
    itemPrice number(10,2) not null
);
grant insert, update, delete, select on foodItem to public;

--5 
create table member (
    memberID integer primary key not null,
    tierID integer not null references membershipTier(tierID),
    firstName varchar2(50) not null,
    lastName varchar2(50) not null,
    phone varchar2(20),
    email varchar2(50),
    dateOfBirth timestamp not null,
    emergencyContactName varchar2(50),
    emergencyContactPhone varchar2(20)
    -- Do we want to have atleast one of phone or email for a particular member to be present?
);
grant insert, update, delete, select on member to public;

--6
create table pet (
    petID integer primary key not null,
    roomId integer references room(roomID), -- Can not be in any room at a particular moment
    breedID integer not null references breed(breedID),
    name varchar2(50), -- You may have not named a pet yet
    age integer, -- You may have not determined its age yet
    dateOfArrival timestamp default sysdate not null,
    specialNeeds varchar2(100),
    temperament varchar2(100), --Friendly, Shy, Loud etc.
    status varchar2(50), -- May not have a designated role yet, can be 'staff' or 'up for adoption' or nothing yet
    constraint chkPetStatus check (status in ('in care', 'available', 'adoption', 'adopted', 'deceased'))
);
grant insert, update, delete, select on pet to public;

--7 
create table employee (
    employeeID integer primary key not null,
    firstName varchar2(50) not null,
    lastName varchar2(50) not null,
    role varchar2(50) not null
);
grant insert, update, delete, select on employee to public;

--8
create table reservation (
    reservationID integer primary key not null,
    memberID integer not null references member(memberID),
    roomID integer not null references room(roomID),
    reservationStart timestamp not null,
    reservationEnd timestamp not null,
    checkIn timestamp,
    checkOut timestamp,
    tierAtTimeOfBooking varchar2(50) not null, -- Stores the tier name, which functionally determines the tier id 
    constraint chkReservationTimeContinuity check(checkIn >= reservationStart)
);
grant insert, update, delete, select on reservation to public;

--9
create table application (
    applicationID integer primary key not null,
    memberID integer not null references member(memberID),
    petID integer not null references pet(petID),
    employeeID integer not null references employee(employeeID),
    applicationDate timestamp default sysdate not null,
    status varchar2(50) not null,
    constraint chkAppStatus check (status in ('pending', 'under review', 'approved', 'rejected', 'adopted')) -- Brainstorm statuses
);
grant insert, update, delete, select on application to public;

--10 
create table orders (
    ordersID integer primary key not null,
    memberID integer references member(memberID),
    employeeID integer not null references employee(employeeID),
    reservationID integer references reservation(reservationID),
    -- The idea here is that we can either have a member order casually have a catering order for a reservation, so either can be null but both cannot be null at the same time
    orderDate timestamp not null,
    amountOwing number(10,2) not null,
    status varchar2(50) not null,
    constraint chkWhoOrdered check (memberID is not null or reservationID is not null),
    constraint chkOrderStatus check (status in ('pending', 'complete', 'started', 'canceled'))
    -- Need to discuss how we can refund an order when it gets canceled
);
grant insert, update, delete, select on orders to public;

--11
create table event (
    eventID integer primary key not null,
    roomID integer not null references room(roomID),
    employeeID integer not null references employee(employeeID),
    eventName varchar2(50),
    eventDate timestamp not null,
    startTime timestamp not null,
    maxCapacity integer,
    constraint chkEventTimeContinuity check (startTime >= eventDate)
);
grant insert, update, delete, select on event to public;


--12
create table healthRecord (
    recordID integer primary key not null,
    petID integer not null references pet(petID),
    employeeID integer not null references employee(employeeID),
    recordDate timestamp not null,
    recordType varchar2(50) not null, -- Regular checkup, dental checkup etc.
    description varchar2(255),
    notes varchar2(255), -- Explanation regarding void/correction
    nextDueDate timestamp,
    status varchar2(50) default 'active',
    constraint chkRecordStatus check (status in ('active', 'void', 'corrected')),
    constraint chkRecordDateDiscontinuity check(nextDueDate > recordDate)
);
grant insert, update, delete, select on healthRecord to public;

--13 
create table orderLine (
    ordersID integer not null references orders(ordersID),
    itemID integer not null references foodItem(itemID),
    quantity integer not null,
    priceAtPurchase number(10,2) not null,
    primary key (ordersID, itemID)
);
grant insert, update, delete, select on orderLine to public;

--14
create table adoption (
    adoptionID integer primary key not null,
    applicationID integer not null unique references application(applicationID),
    adoptionDate timestamp not null,
    adoptionFee number(10,2),
    followUpSchedule timestamp,
    constraint chkAdoptionTimeContinuity check (followUpSchedule > adoptionDate)
);
grant insert, update, delete, select on adoption to public;

--15
create table eventBooking (
    bookingID integer primary key not null,
    memberID integer not null references member(memberID),
    eventID integer not null references event(eventID),
    bookingDate timestamp not null,
    attendanceStatus varchar2(20) default 'registered' not null,
    paymentStatus varchar2(50) default 'unpaid' not null,
    constraint chkBookingStatus check (paymentStatus in ('paid', 'unpaid')),
    constraint chkAttendanceStatus check (attendanceStatus in ('registered', 'attended', 'no-show', 'canceled'))
);
grant insert, update, delete, select on eventBooking to public;