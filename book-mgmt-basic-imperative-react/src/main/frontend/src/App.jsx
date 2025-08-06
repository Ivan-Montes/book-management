import { useState, useEffect } from 'react';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import Header from './components/Header';
import Home from './views/Home';
import Books from './views/Books';
import Publishers from './views/Publishers';
import Genres from './views/Genres';
import Authors from './views/Authors';
import Bookshops from './views/Bookshops';
import BookBookshops from './views/BookBookshops';
import Login from './views/Login';
import Footer from './components/Footer';
import 'bootstrap/dist/css/bootstrap.min.css';
import SvgSymbols from './components/SvgSymbols';
import Success from './components/Success';
import GenreFormContainer from './containers/GenreFormContainer';
import PublisherFormContainer from './containers/PublisherFormContainer';
import AuthorFormContainer from './containers/AuthorFormContainer';
import BookshopFormContainer from './containers/BookshopFormContainer';
import BookBookshopFormContainer from './containers/BookBookshopFormContainer';
import BookBookshopCreateContainer from './containers/BookBookshopCreateContainer';
import BookFormContainer from './containers/BookFormContainer';

function App() {
  const [theme, setTheme] = useState('dark');

  useEffect(() => {
    document.documentElement.setAttribute('data-bs-theme', theme);
  }, [theme]);

  const toggleTheme = () => {
    setTheme(prev => (prev === 'light' ? 'dark' : 'light'));
  };

  return (
    <BrowserRouter>
      <SvgSymbols />
      <Header />
      <main className="container-fluid">
        <button 
          onClick={toggleTheme}
          className="btn btn-sm btn-outline-secondary mb-3"
        >
          Change to {theme === 'light' ? 'dark mode' : 'light mode'}
        </button>
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/books" element={<Books />} />
          <Route path="/publishers" element={<Publishers />} />
          <Route path="/genres" element={<Genres />} />
          <Route path="/authors" element={<Authors />} />
          <Route path="/bookshops" element={<Bookshops />} />
          <Route path="/bookBookshops" element={<BookBookshops />} />
          <Route path="/login" element={<Login />} />
		  <Route path="/success" element={<Success />} />
          <Route path="/add-genre" element={<GenreFormContainer />} />
          <Route path="/edit-genre/:id" element={<GenreFormContainer />} />
		  <Route path="/add-publisher" element={<PublisherFormContainer />} />
		  <Route path="/edit-publisher/:id" element={<PublisherFormContainer />} />
		  <Route path="/add-author" element={<AuthorFormContainer />} />
		  <Route path="/edit-author/:id" element={<AuthorFormContainer />} />
		  <Route path="/add-bookshop" element={<BookshopFormContainer />} />
		  <Route path="/edit-bookshop/:id" element={<BookshopFormContainer />} />
		  <Route path="/add-bookbookshop" element={<BookBookshopCreateContainer />} />
		  <Route path="/edit-bookbookshop/book/:bookId/bookshop/:bookshopId" element={<BookBookshopFormContainer />} />
		  <Route path="/add-book" element={<BookFormContainer />} />
		  <Route path="/edit-book/:id" element={<BookFormContainer />} />
		  </Routes>
      </main>
      <Footer />
    </BrowserRouter>
  );
}

export default App;
