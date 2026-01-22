import { Link, useNavigate } from 'react-router-dom';

const Navbar = () => {
    const navigate = useNavigate();
    const token = localStorage.getItem('token');

    const handleLogout = () => {
        localStorage.removeItem('token');
        navigate('/login');
    };

    return (
        <nav className="bg-white shadow-sm border-b border-gray-200">
            <div className="container mx-auto px-4 py-3 flex justify-between items-center">
                <Link to="/dashboard" className="text-xl font-bold text-indigo-600 flex items-center">
                    📈 PortfolioTracker
                </Link>
                <div>
                    {token ? (
                        <button
                            onClick={handleLogout}
                            className="text-gray-600 hover:text-red-500 font-medium transition"
                        >
                            Logout
                        </button>
                    ) : (
                        <div className="space-x-4">
                            <Link to="/login" className="text-gray-600 hover:text-indigo-600">Login</Link>
                            <Link to="/register" className="bg-indigo-600 text-white px-4 py-2 rounded hover:bg-indigo-700">Get Started</Link>
                        </div>
                    )}
                </div>
            </div>
        </nav>
    );
};

export default Navbar;