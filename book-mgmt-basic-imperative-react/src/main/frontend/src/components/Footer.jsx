import React from 'react';
import { Link } from 'react-router-dom';

function Footer() {
  return (
    <footer className="py-3 my-4 border-top">
      <ul className="nav justify-content-center border-bottom pb-3 mb-3">
        <li className="nav-item">
          <Link to="/" className="nav-link px-2 text-muted">Home</Link>
        </li>
        <li className="nav-item">
          <Link to="/books" className="nav-link px-2 text-muted">Books</Link>
        </li>
        <li className="nav-item">
          <Link to="/publishers" className="nav-link px-2 text-muted">Publishers</Link>
        </li>
        <li className="nav-item">
          <Link to="/genres" className="nav-link px-2 text-muted">Genres</Link>
        </li>
        <li className="nav-item">
          <Link to="/authors" className="nav-link px-2 text-muted">Authors</Link>
        </li>
        <li className="nav-item">
          <Link to="/bookshops" className="nav-link px-2 text-muted">Bookshops</Link>
        </li>
        <li className="nav-item">
          <Link to="/bookBookshops" className="nav-link px-2 text-muted">Book & Bookshops</Link>
        </li>
      </ul>
      <p className="text-center text-muted">© 2049 Tyrell Corporation</p>
    </footer>
  );
}

export default Footer;
