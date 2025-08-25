import React from 'react';

type SuccessModalProps = {
  show: boolean;
  title: string;
  message: string;
  onConfirm: () => void;
  isLoading: boolean;
};

const SuccessModal: React.FC<SuccessModalProps> = ({
  show,
  title,
  message,
  onConfirm,
  isLoading,
}) => {
  return (
    <div
      className={`modal fade ${show ? 'show d-block' : 'd-none'}`}
      tabIndex={-1}
      role="dialog"
      style={{ backgroundColor: 'rgba(0,0,0,0.5)' }}
      aria-modal="true"
    >
      <div className="modal-dialog modal-dialog-centered" role="document">
        <div className="modal-content border-success">
          <div className="modal-header bg-success text-white">
            <h5 className="modal-title">{title}</h5>
          </div>
          <div className="modal-body">
            <p>{message}</p>
          </div>
          <div className="modal-footer">
            <button
              type="button"
              className="btn btn-success"
              onClick={onConfirm}
              disabled={isLoading}
            >
              {isLoading ? (
                <>
                  <span
                    className="spinner-border spinner-border-sm me-2"
                    role="status"
                    aria-hidden="true"
                  ></span>
                  Loading...
                </>
              ) : (
                'OK'
              )}
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default SuccessModal;
