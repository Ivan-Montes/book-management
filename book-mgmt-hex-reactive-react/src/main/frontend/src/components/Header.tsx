import React from 'react';
import { Link } from 'react-router-dom';
import springLogo from '../assets/images/spring-logo.svg';

const Header: React.FC = () => {
  return (
    <header className="d-flex flex-wrap align-items-center justify-content-center justify-content-md-between py-3 mb-4 border-bottom">
      <Link
        to="/"
        className="d-flex align-items-center col-md-3 mb-2 mb-md-0 text-decoration-none"
      >
        <img
          className="d-block mx-auto mb-4"
          src={springLogo}
          alt="Spring Logo"
          width="72"
          height="57"
        />
      </Link>

      <ul className="nav col-12 col-md-auto mb-2 justify-content-center mb-md-0">
        <li><Link to="/" className="nav-link px-2 text-muted">Home</Link></li>
        <li><Link to="/books" className="nav-link px-2 text-muted">Books</Link></li>
        <li><Link to="/publishers" className="nav-link px-2 text-muted">Publishers</Link></li>
        <li><Link to="/genres" className="nav-link px-2 text-muted">Genres</Link></li>
        <li><Link to="/authors" className="nav-link px-2 text-muted">Authors</Link></li>
        <li><Link to="/bookshops" className="nav-link px-2 text-muted">Bookshops</Link></li>
        <li><Link to="/bookBookshops" className="nav-link px-2 text-muted">Book & Bookshops</Link></li>
      </ul>

      <div className="col-md-3 text-end">
        <Link to="/login" className="btn btn-outline-primary me-2">Login</Link>
        <button type="button" className="btn btn-primary disabled">Sign-up</button>
      </div>
    </header>
  );
};

export default Header;
