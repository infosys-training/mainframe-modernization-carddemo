import { useNavigate } from 'react-router-dom';

const adminItems = [
  { label: 'List Users', description: 'Browse all system users', path: '/admin/users', icon: '👥' },
  { label: 'Add User', description: 'Create a new user account', path: '/admin/users/add', icon: '➕' },
];

export function AdminMenuPage() {
  const navigate = useNavigate();

  return (
    <div>
      <h2 className="text-2xl font-bold text-gray-800 mb-6">Admin Menu</h2>
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        {adminItems.map((item) => (
          <button
            key={item.path}
            onClick={() => navigate(item.path)}
            className="p-6 bg-white rounded-lg shadow-sm border border-gray-200 hover:shadow-md hover:border-blue-300 transition-all text-left"
          >
            <div className="text-2xl mb-2">{item.icon}</div>
            <h3 className="font-semibold text-gray-800">{item.label}</h3>
            <p className="text-sm text-gray-500 mt-1">{item.description}</p>
          </button>
        ))}
      </div>
    </div>
  );
}
