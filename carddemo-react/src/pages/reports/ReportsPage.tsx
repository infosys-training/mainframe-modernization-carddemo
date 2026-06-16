import { useState, type FormEvent } from 'react';
import { useNavigate } from 'react-router-dom';
import { reportApi } from '@/api';
import { ErrorMessage } from '@/components/common/ErrorMessage';
import { ConfirmDialog } from '@/components/common/ConfirmDialog';

type ReportType = 'monthly' | 'yearly' | 'custom';

export function ReportsPage() {
  const navigate = useNavigate();
  const [reportType, setReportType] = useState<ReportType>('monthly');
  const [startDate, setStartDate] = useState('');
  const [endDate, setEndDate] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const [showConfirm, setShowConfirm] = useState(false);

  const handleSubmit = (e: FormEvent) => {
    e.preventDefault();
    setError(null);

    if (reportType === 'custom') {
      if (!startDate || !endDate) {
        setError('Start Date and End Date are required for custom reports');
        return;
      }
    }

    setShowConfirm(true);
  };

  const handleConfirm = async () => {
    setShowConfirm(false);
    setLoading(true);
    setError(null);
    try {
      const result = await reportApi.generate({
        type: reportType,
        startDate: reportType === 'custom' ? startDate : undefined,
        endDate: reportType === 'custom' ? endDate : undefined,
      });
      setSuccess(result.message || 'Report generation submitted successfully');
    } catch {
      setError('Failed to generate report');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <h2 className="text-2xl font-bold text-gray-800">Reports</h2>
        <button onClick={() => navigate(-1)} className="text-blue-600 hover:text-blue-800">
          &larr; Back
        </button>
      </div>

      <ErrorMessage message={error} />
      <ErrorMessage message={success} type="success" />

      <form onSubmit={handleSubmit} className="bg-white rounded-lg shadow-sm border p-6 max-w-lg space-y-6">
        <div className="space-y-3">
          <label className="block text-sm font-medium text-gray-700">Report Type</label>
          <div className="space-y-2">
            <label className="flex items-center gap-2">
              <input type="radio" name="reportType" value="monthly" checked={reportType === 'monthly'} onChange={() => setReportType('monthly')} className="text-blue-600" />
              <span>Monthly (Current Month)</span>
            </label>
            <label className="flex items-center gap-2">
              <input type="radio" name="reportType" value="yearly" checked={reportType === 'yearly'} onChange={() => setReportType('yearly')} className="text-blue-600" />
              <span>Yearly (Current Year)</span>
            </label>
            <label className="flex items-center gap-2">
              <input type="radio" name="reportType" value="custom" checked={reportType === 'custom'} onChange={() => setReportType('custom')} className="text-blue-600" />
              <span>Custom Date Range</span>
            </label>
          </div>
        </div>

        {reportType === 'custom' && (
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-sm text-gray-600 mb-1">Start Date (MM/DD/YYYY)</label>
              <input type="date" value={startDate} onChange={(e) => setStartDate(e.target.value)} className="w-full px-3 py-2 border rounded-md focus:ring-2 focus:ring-blue-500" />
            </div>
            <div>
              <label className="block text-sm text-gray-600 mb-1">End Date (MM/DD/YYYY)</label>
              <input type="date" value={endDate} onChange={(e) => setEndDate(e.target.value)} className="w-full px-3 py-2 border rounded-md focus:ring-2 focus:ring-blue-500" />
            </div>
          </div>
        )}

        <button type="submit" disabled={loading} className="px-6 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-500 disabled:bg-gray-400">
          {loading ? 'Generating...' : 'Generate Report'}
        </button>
      </form>

      <ConfirmDialog
        open={showConfirm}
        title="Confirm Report Generation"
        message={`Generate ${reportType} report?`}
        onConfirm={handleConfirm}
        onCancel={() => setShowConfirm(false)}
      />
    </div>
  );
}
