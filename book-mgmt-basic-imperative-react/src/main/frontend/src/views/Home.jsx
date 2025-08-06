import React from 'react';
import { Link } from 'react-router-dom';
import springLogo from '../assets/images/spring-logo.svg'

function Home() {
  return (
	
    <div className="container px-4 pb-5">
      <div className="text-center my-5">
        <img
          className="d-block mx-auto mb-4"
          src={springLogo}
          alt="Spring Logo"
          width="72"
          height="57"
        />
        <h1 className="display-5 fw-bold text-body-emphasis">Welcome</h1>
        <div className="col-lg-6 mx-auto">
          <p className="lead mb-4">
            Welcome to your own book collection management with Spring Framework,
            the world’s most popular open source toolkit
          </p>
        </div>
      </div>

      <h2 className="pb-2 border-bottom">Sections</h2>
      <div className="row g-4 py-5 row-cols-1 row-cols-lg-3">

        {sections.map(({ title, description, icon, path }) => (
          <div className="col d-flex align-items-start" key={title}>
            <div className="icon-square text-body-emphasis bg-body-secondary d-inline-flex align-items-center justify-content-center fs-4 flex-shrink-0 me-3">
              <svg className="bi" width="1em" height="1em">
                <use xlinkHref={`#${icon}`} />
              </svg>
            </div>
            <div>
              <h3 className="fs-2 text-body-emphasis">{title}</h3>
              <p>{description}</p>
              <Link to={path} className="btn btn-primary">
                Go to {title}
              </Link>
            </div>
          </div>
        ))}

      </div>
    </div>
  );
}

const sections = [
  {
    title: 'Books',
    description: 'Create and manage your books with the Book store management feature.',
    icon: 'toggles2',
    path: '/books',
  },
  {
    title: 'Publishers',
    description: 'Add a brave new world of Publishers with this module.',
    icon: 'cpu-fill',
    path: '/publishers',
  },
  {
    title: 'Genres',
    description: 'Configure the literary genres of your books in this section.',
    icon: 'tools',
    path: '/genres',
  },
  {
    title: 'Authors',
    description: 'With the Authors management module, you could add a list of authors to the DB.',
    icon: 'calendar3',
    path: '/authors',
  },
  {
    title: 'Bookshops',
    description: 'Register your Bookshop in the system filling its profile.',
    icon: 'collection',
    path: '/bookshops',
  },
  {
    title: 'Book & Bookshops',
    description: 'Configure or erase the books you are selling in a Bookshop.',
    icon: 'bi-amd',
    path: '/bookBookshops',
  },
];

export default Home;
