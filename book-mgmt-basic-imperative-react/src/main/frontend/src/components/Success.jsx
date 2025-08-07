import React from 'react';
import { Link } from 'react-router-dom';

function Success() {
  return (
    <div className="container mt-5">
      <div className="row">
        <div className="col-lg-6 offset-lg-3 center-div">
          <div className="jumbotron">
            <h1 className="display-4">Congratulations!</h1>
            <p className="lead">You have successfully logged in.</p>
            <hr className="my-4" />
            <p>As a reward, enjoy this classic video:</p>
            <div className="ratio ratio-16x9 mb-4">
              <iframe
                title="Rick Roll"
                className="embed-responsive-item"
                src="https://www.youtube.com/embed/dQw4w9WgXcQ"
                style={{ maxWidth: '100%', height: '100%' }}
                allowFullScreen
              ></iframe>
            </div>
            <Link to="/" className="btn btn-primary">
              Back to Home
            </Link>
          </div>
        </div>
      </div>
    </div>
  );
}

export default Success;
