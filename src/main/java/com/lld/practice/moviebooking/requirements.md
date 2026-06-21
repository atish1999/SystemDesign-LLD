## Requirements

#### Design a movie booking system like book my show

<hr/>

## Core Features

<hr/>

- User will be able to search movies based on title
- User will be able to see what movies are running in a theater
- Theaters will have multiple screens each screen will have same layout [ Row: A- Z  and col: 0 - 26 ]
- user can be able to see available seats and can book those
- User can book multiple seats 
- When tickets get booked user will receive the confirmation of their reservation with an Id and the seat details
- two users can go for booking the same seat but only of them should succeed
- Users can cancel their reservations and those seats will be available for booking

## Out Of Scope

<hr/>

- UI/UX flow
- Payment Integration
- Variable Seat Layouts

<hr/>

## Class Diagram

```mermaid
classDiagram
    
    class BookingSystem {
        -theaters: List~Theatre~
        
        +searchMovies(title: String) List~ShowTime~
        +searchMoviesByTheater(theater: Theatre) List~ShowTime~
        +book(showTime: ShowTime, seatIds: List~String~) Reservation
        +cancelReservation(confirmationId: String)
    }
    
    class Theater {
        -id: String
        -name: String
        -showtimes: List~ShowTime~
        
        +getShowTimes() List~ShowTime~
        +getShowTimesByMovie(movie: Movie) List~ShowTime~
    }
    
    class ShowTime {
        -id: String
        -screenLabel: String
        -theater: Theater
        -reservations: List~Reservation~
        -movie: Movie
        -showTime: LocalDateTime
        
        +book(reservation: Reservation)
        +cancel(reservation: Reservation)
        +getAvailableSeats() List~String~
        +getTheater() Theater
        +isAvailable(seatId: String) Boolean
        +getId() String
        +getScreenLables() String
        -isValidSeatIds(seatIds: List~String~) Boolean
    }
    
    class Reservation {
        -confirmationId: String
        -showTime: ShowTime
        -seatIds: List~String~
        
        +getConfirmationId() String
        +getSeats() List~String~
        +getShowTime() ShowTime
    }
    
    class Movie {
        -id: String
        -title: String
        
        +getId() String
        +getTitle() String
    }
    
    BookingSystem "1"--> "*" Theater
    Theater "1" --> "*" ShowTime
    ShowTime "*" --> "1" Theater
   
   
```
