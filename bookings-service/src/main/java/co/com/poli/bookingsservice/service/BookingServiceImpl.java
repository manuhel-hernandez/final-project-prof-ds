package co.com.poli.bookingsservice.service;

import co.com.poli.bookingsservice.clientFeign.MovieClient;
import co.com.poli.bookingsservice.clientFeign.ShowtimeClient;
import co.com.poli.bookingsservice.clientFeign.UserClient;
import co.com.poli.bookingsservice.model.Movie;
import co.com.poli.bookingsservice.model.Showtime;
import co.com.poli.bookingsservice.model.User;
import co.com.poli.bookingsservice.persistance.entity.Booking;
import co.com.poli.bookingsservice.persistance.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService{

    private final BookingRepository bookingRepository;

    @Qualifier("co.com.poli.bookingsservice.clientFeign.MovieClient")
    private final MovieClient movieClient;

    @Qualifier("co.com.poli.bookingsservice.clientFeign.UserClient")
    private final UserClient userClient;

    @Qualifier("co.com.poli.bookingsservice.clientFeign.ShowtimeClient")
    private final ShowtimeClient showtimeClient;

    @Override
    @Transactional(readOnly = true)
    public List<Booking> findAll() {
        List<Booking> bookingList = bookingRepository.findAll();
        for (Booking booking : bookingList) {
            getUserInfo(booking);
            getShowtimeInfo(booking);
            if(booking.getMovies() != null && !booking.getMovies().isEmpty()){
                getMovieInfo(booking);
            }
        }
        return bookingList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(Booking booking) {
        bookingRepository.save(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public Booking findById(String id) {
        Booking booking = bookingRepository.findById(Long.valueOf(id)).orElse(null);
        if(booking == null){
            return booking;
        }
        getUserInfo(booking);
        getShowtimeInfo(booking);
        if(booking.getMovies() == null || booking.getMovies().isEmpty()){
            return booking;
        }
        getMovieInfo(booking);
        return booking;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(String id) {
        bookingRepository.deleteById(Long.valueOf(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Booking> findByUserId(String id) {
        List<Booking> bookingList = bookingRepository.findAll().stream().
                filter(booking -> Objects.equals(booking.getUserid(), Long.valueOf(id))).collect(Collectors.toList());
        for (Booking booking : bookingList) {
            getUserInfo(booking);
            getShowtimeInfo(booking);
            if(booking.getMovies() != null && !booking.getMovies().isEmpty()){
                getMovieInfo(booking);
            }
        }
        return bookingList;
    }

    @Override
    @Transactional(readOnly = true)
    public Boolean checkIfMovieIsAssigned(String id) {
        List<Booking> bookingList = bookingRepository.findAll().stream().
                filter(booking -> booking.getMovies().contains(Long.valueOf(id))).collect(Collectors.toList());
        return !bookingList.isEmpty();
    }

    private void getMovieInfo(Booking booking) {
        List<Movie> movies = new ArrayList<>();
        ModelMapper modelMapper = new ModelMapper();
        for (Long movieId : booking.getMovies()) {
            Movie movie = modelMapper.map(movieClient.findByID(movieId.toString()).getData(), Movie.class);
            movies.add(movie);
        }
        booking.setMovies_list(movies);
    }

    private void getUserInfo(Booking booking){
        ModelMapper modelMapper = new ModelMapper();
        User user = modelMapper.map(userClient.findByID(booking.getUserid().toString()).getData(), User.class);
        booking.setUser(user);
    }

    private void getShowtimeInfo(Booking booking){
        ModelMapper modelMapper = new ModelMapper();
        Showtime showtime = modelMapper.map(showtimeClient.findByID(booking.getShowtimeid().toString()).getData(), Showtime.class);
        booking.setShowtime(showtime);
    }

}
