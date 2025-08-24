import { useState, useEffect } from 'react';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import 'bootstrap/dist/css/bootstrap.min.css';
import Header from './components/Header'
import Footer from './components/Footer';
import SvgSymbols from './components/SvgSymbols';
import Success from './components/Success';
import GenreFormContainer from './containers/GenreFormContainer';
import AuthorFormContainer from './containers/AuthorFormContainer';
import BookshopFormContainer from './containers/BookshopFormContainer';
import PublisherFormContainer from './containers/PublisherFormContainer';
import BookFormContainer from './containers/BookFormContainer';
import BookBookshopFormContainer from './containers/BookBookshopFormContainer';
import BookBookshopCreateContainer from './containers/BookBookshopCreateContainer';
import Home from './views/Home';
import Genres from './views/Genres';
import Authors from './views/Authors';
import Bookshops from './views/Bookshops';
import Publishers from './views/Publishers';
import Login from './views/Login';
import Books from './views/Books';
import BookBookshops from './views/BookBookshops';

const App = () => {
  const [theme, setTheme] = useState<'light' | 'dark'>('dark');

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
		  <Route path="/login" element={<Login />} />
		  <Route path="/success" element={<Success />} />
		  <Route path="/genres" element={<Genres />} />
		  <Route path="/add-genre" element={<GenreFormContainer />} />
		  <Route path="/edit-genre/:id" element={<GenreFormContainer />} />
		  <Route path="/authors" element={<Authors />} />
		  <Route path="/add-author" element={<AuthorFormContainer />} />
		  <Route path="/edit-author/:id" element={<AuthorFormContainer />} />
		  <Route path="/bookshops" element={<Bookshops />} />
		  <Route path="/add-bookshop" element={<BookshopFormContainer />} />
		  <Route path="/edit-bookshop/:id" element={<BookshopFormContainer />} />
		  <Route path="/publishers" element={<Publishers />} />
		  <Route path="/add-publisher" element={<PublisherFormContainer />} />
		  <Route path="/edit-publisher/:id" element={<PublisherFormContainer />} />
		  <Route path="/books" element={<Books />} />
		  <Route path="/add-book" element={<BookFormContainer />} />
		  <Route path="/edit-book/:id" element={<BookFormContainer />} />
		  <Route path="/bookBookshops" element={<BookBookshops />} />
		  <Route path="/add-bookbookshop" element={<BookBookshopCreateContainer />} />
		  <Route path="/edit-bookbookshop/book/:bookId/bookshop/:bookshopId" element={<BookBookshopFormContainer />} />
        </Routes>
      </main>
      <Footer />
    </BrowserRouter>
  );
};

export default App;
