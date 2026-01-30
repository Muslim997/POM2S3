'use client';

import { useEffect, useState, useCallback } from 'react';
import { useRouter } from 'next/navigation';
import Layout from '@/components/Layout';
import { Card, CardHeader, CardContent, CardTitle } from '@/components/Card';
import { useAuthStore } from '@/lib/store';
import { FileText, Calendar, CheckCircle, Clock, AlertCircle } from 'lucide-react';
import Link from 'next/link';
import Button from '@/components/Button';
import { format, isPast, differenceInDays } from 'date-fns';
import { fr } from 'date-fns/locale';

export default function AssignmentsPage() {
  const router = useRouter();
  const { user } = useAuthStore();
  const [assignments, setAssignments] = useState<any[]>([]);
  const [submissions, setSubmissions] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [filter, setFilter] = useState<'all' | 'pending' | 'submitted' | 'graded'>('all');

  const loadAssignments = useCallback(async () => {
    if (!user) return;

    try {
      const token = localStorage.getItem('token');
      if (!token) {
        router.push('/login');
        return;
      }

      const response = await fetch('/api/assignments', {
        headers: {
          'Authorization': `Bearer ${token}`,
        },
      });

      if (response.ok) {
        const data = await response.json();
        setAssignments(data.assignments || []);
        setSubmissions(data.submissions || []);
      } else {
        console.error('Erreur lors de la récupération des assignments');
      }
    } catch (error) {
      console.error('Error loading assignments:', error);
    } finally {
      setLoading(false);
    }
  }, [user, router]);

  useEffect(() => {
    if (!user) {
      router.push('/login');
      return;
    }

    loadAssignments();
  }, [user, router, loadAssignments]);

  const getAssignmentStatus = (assignment: any) => {
    if (user?.role !== 'student') return null;

    const submission = submissions.find(s => s.assignment_id === assignment.id);

    if (!submission) {
      return {
        label: 'À faire',
        color: 'text-orange-600 bg-orange-50',
        icon: Clock,
      };
    }

    if (submission.grades && submission.grades.length > 0) {
      return {
        label: 'Noté',
        color: 'text-green-600 bg-green-50',
        icon: CheckCircle,
      };
    }

    if (submission.status === 'submitted') {
      return {
        label: 'Soumis',
        color: 'text-blue-600 bg-blue-50',
        icon: CheckCircle,
      };
    }

    return {
      label: 'Brouillon',
      color: 'text-gray-600 bg-gray-50',
      icon: Clock,
    };
  };

  const getDaysUntilDue = (dueDate: string) => {
    return differenceInDays(new Date(dueDate), new Date());
  };

  const filteredAssignments = assignments.filter(assignment => {
    if (filter === 'all') return true;
    const status = getAssignmentStatus(assignment);

    if (filter === 'pending') return status?.label === 'À faire';
    if (filter === 'submitted') return status?.label === 'Soumis';
    if (filter === 'graded') return status?.label === 'Noté';

    return true;
  });

  if (!user) return null;

  return (
    <Layout>
      <div className="mb-8">
        <div className="flex items-center justify-between mb-6">
          <div>
            <h1 className="text-3xl font-bold text-gray-900 mb-2">Devoirs</h1>
            <p className="text-gray-600">
              {user.role === 'student'
                ? 'Suivez et soumettez vos devoirs'
                : 'Gérez les devoirs de vos cours'}
            </p>
          </div>
        </div>

        {user.role === 'student' && (
          <div className="flex space-x-2 mb-6">
            <button
              onClick={() => setFilter('all')}
              className={`px-4 py-2 rounded-lg font-medium transition-colors ${
                filter === 'all'
                  ? 'bg-blue-600 text-white'
                  : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
              }`}
            >
              Tous
            </button>
            <button
              onClick={() => setFilter('pending')}
              className={`px-4 py-2 rounded-lg font-medium transition-colors ${
                filter === 'pending'
                  ? 'bg-blue-600 text-white'
                  : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
              }`}
            >
              À faire
            </button>
            <button
              onClick={() => setFilter('submitted')}
              className={`px-4 py-2 rounded-lg font-medium transition-colors ${
                filter === 'submitted'
                  ? 'bg-blue-600 text-white'
                  : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
              }`}
            >
              Soumis
            </button>
            <button
              onClick={() => setFilter('graded')}
              className={`px-4 py-2 rounded-lg font-medium transition-colors ${
                filter === 'graded'
                  ? 'bg-blue-600 text-white'
                  : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
              }`}
            >
              Notés
            </button>
          </div>
        )}
      </div>

      {loading ? (
        <div className="flex justify-center items-center h-64">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
        </div>
      ) : filteredAssignments.length === 0 ? (
        <Card>
          <CardContent className="text-center py-12">
            <FileText size={48} className="mx-auto text-gray-400 mb-4" />
            <p className="text-gray-600 mb-4">Aucun devoir trouvé</p>
          </CardContent>
        </Card>
      ) : (
        <div className="space-y-4">
          {filteredAssignments.map((assignment) => {
            const status = getAssignmentStatus(assignment);
            const daysUntilDue = getDaysUntilDue(assignment.due_date);
            const isOverdue = isPast(new Date(assignment.due_date));
            const StatusIcon = status?.icon || Clock;

            return (
              <Card key={assignment.id} className="hover:shadow-md transition-shadow">
                <CardContent>
                  <div className="flex items-start justify-between">
                    <div className="flex-1">
                      <div className="flex items-center space-x-3 mb-2">
                        <h3 className="text-lg font-semibold text-gray-900">
                          {assignment.title}
                        </h3>
                        {status && (
                          <span className={`px-2 py-1 rounded-full text-xs font-medium ${status.color} flex items-center`}>
                            <StatusIcon size={12} className="mr-1" />
                            {status.label}
                          </span>
                        )}
                        {isOverdue && status?.label === 'À faire' && (
                          <span className="px-2 py-1 rounded-full text-xs font-medium text-red-600 bg-red-50 flex items-center">
                            <AlertCircle size={12} className="mr-1" />
                            En retard
                          </span>
                        )}
                      </div>

                      <p className="text-sm text-gray-600 mb-3">
                        {assignment.subjects?.code} - {assignment.subjects?.title}
                      </p>

                      <p className="text-gray-700 mb-4">{assignment.description}</p>

                      <div className="flex items-center text-sm text-gray-600 space-x-4">
                        <div className="flex items-center">
                          <Calendar size={16} className="mr-2" />
                          <span>
                            Date limite: {format(new Date(assignment.due_date), 'PPP', { locale: fr })}
                          </span>
                        </div>
                        <span>•</span>
                        <span>{assignment.max_points} points</span>
                        {!isOverdue && daysUntilDue <= 3 && status?.label === 'À faire' && (
                          <>
                            <span>•</span>
                            <span className="text-orange-600 font-medium">
                              {daysUntilDue === 0 ? 'Aujourd\'hui' : `${daysUntilDue} jour(s) restant(s)`}
                            </span>
                          </>
                        )}
                      </div>
                    </div>

                    <Link href={`/assignments/${assignment.id}`}>
                      <Button variant="secondary">
                        {user.role === 'student' ? 'Voir le devoir' : 'Gérer'}
                      </Button>
                    </Link>
                  </div>
                </CardContent>
              </Card>
            );
          })}
        </div>
      )}
    </Layout>
  );
}
