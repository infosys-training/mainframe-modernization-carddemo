import { useNavigate } from 'react-router-dom';

const menuItems = [
  { label: 'View Account', description: 'View account details and balances', path: '/accounts/0', icon: '🏦' },
  { label: 'Update Account', description: 'Update account information', path: '/accounts/0/edit', icon: '✏️' },
  { label: 'Credit Card List', description: 'Browse and manage credit cards', path: '/cards', icon: '💳' },
  { label: 'Transaction List', description: 'View transaction history', path: '/transactions', icon: '📋' },
  { label: 'Add Transaction', description: 'Create a new transaction', path: '/transactions/add', icon: '➕' },
  { label: 'Reports', description: 'Generate financial reports', path: '/reports', icon: '📊' },
  { label: 'Bill Payment', description: 'Pay your account balance', path: '/billing', icon: '💵' },
];

export function MainMenuPage() {
  const navigate = useNavigate();

  return (
    <div>
      <h2 className="text-2xl font-bold text-gray-800 mb-6">Main Menu</h2>
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        {menuItems.map((item) => (
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
