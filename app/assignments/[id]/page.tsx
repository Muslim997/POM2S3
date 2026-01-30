'use client';

import { useEffect, useState, useCallback } from 'react';
import { useRouter, useParams } from 'next/navigation';
import Layout from '@/components/Layout';
import { Card, CardHeader, CardContent, CardTitle } from '@/components/Card';
import { useAuthStore } from '@/lib/store';
import { FileText, Calendar, Upload, CheckCircle } from 'lucide-react';
import Button from '@/components/Button';
import { format } from 'date-fns';
import { fr } from 'date-fns/locale';

export default function AssignmentDetailPage() {
  const router = useRouter();
  const params = useParams();
  const { user } = useAuthStore();
  const [assignment, setAssignment] = useState<any>(null);
  const [submission, setSubmission] = useState<any>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!user) {
      router.push('/login');
      return;
    }

    loadAssignment();
  }, [user, params.id, router, loadAssignment]);

  const loadAssignment = useCallback(async () => {
    if (!params.id) return;

    try {
      const token = localStorage.getItem('token');
      const response = await fetch(`/api/assignments/${params.id}`, {
        headers: {
          'Authorization': `Bearer ${token}`,
        },
      });

      if (response.ok) {
        const data = await response.json();
        setAssignment(data.assignment);
        setSubmission(data.submission);
      } else {
        console.error('Erreur lors du chargement du devoir');
      }
    } catch (error) {
      console.error('Error loading assignment:', error);
    } finally {
      setLoading(false);
    }
  }, [params.id]);

  if (!user || loading) {
    return (
      <Layout>
        <div className="flex justify-center items-center h-64">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
        </div>
      </Layout>
    );
  }

  if (!assignment) {
    return (
      <Layout>
        <Card>
          <CardContent className="text-center py-12">
            <p className="text-gray-600">Devoir non trouvé</p>
          </CardContent>
        </Card>
      </Layout>
    );
  }

  const isStudent = user.role === 'student';
  const hasSubmitted = submission && submission.status === 'submitted';
  const isGraded = submission && submission.grades && submission.grades.length > 0;

  return (
    <Layout>
      <div className="max-w-4xl mx-auto">
        <div className="mb-8">
          <div className="flex items-center justify-between">
            <div>
              <h1 className="text-3xl font-bold text-gray-900 mb-2">{assignment.title}</h1>
              <p className="text-gray-600">
                {assignment.subjects?.code} - {assignment.subjects?.title}
              </p>
            </div>
            <div className="text-right">
              <p className="text-sm text-gray-600">Date limite</p>
              <p className="font-medium">
                {format(new Date(assignment.due_date), 'PPP', { locale: fr })}
              </p>
            </div>
          </div>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          <div className="lg:col-span-2">
            <Card>
              <CardHeader>
                <CardTitle>Description</CardTitle>
              </CardHeader>
              <CardContent>
                <p className="text-gray-700 whitespace-pre-wrap">{assignment.description}</p>

                <div className="mt-6 pt-6 border-t border-gray-200">
                  <div className="flex items-center justify-between text-sm">
                    <span className="text-gray-600">Points maximum:</span>
                    <span className="font-medium">{assignment.max_points} points</span>
                  </div>
                </div>
              </CardContent>
            </Card>

            {isStudent && (
              <Card className="mt-6">
                <CardHeader>
                  <CardTitle>Votre soumission</CardTitle>
                </CardHeader>
                <CardContent>
                  {hasSubmitted ? (
                    <div className="space-y-4">
                      <div className="flex items-center space-x-3 p-3 bg-green-50 rounded-lg">
                        <CheckCircle className="text-green-600" size={20} />
                        <div>
                          <p className="font-medium text-green-700">Devoir soumis</p>
                          <p className="text-sm text-green-600">
                            Soumis le {format(new Date(submission.created_at), 'PPP', { locale: fr })}
                          </p>
                        </div>
                      </div>

                      {isGraded && (
                        <div className="p-3 bg-blue-50 rounded-lg">
                          <p className="font-medium text-blue-700">
                            Note: {submission.grades[0].score}/{assignment.max_points}
                          </p>
                          {submission.grades[0].comments && (
                            <p className="text-sm text-blue-600 mt-1">
                              Commentaire: {submission.grades[0].comments}
                            </p>
                          )}
                        </div>
                      )}
                    </div>
                  ) : (
                    <div className="text-center py-8">
                      <Upload size={48} className="mx-auto text-gray-400 mb-4" />
                      <p className="text-gray-600 mb-4">Vous n'avez pas encore soumis ce devoir</p>
                      <Button onClick={() => alert('Fonctionnalité à implémenter : Soumettre le devoir')}>
                        Soumettre le devoir
                      </Button>
                    </div>
                  )}
                </CardContent>
              </Card>
            )}
          </div>

          <div>
            <Card>
              <CardHeader>
                <CardTitle>Informations</CardTitle>
              </CardHeader>
              <CardContent className="space-y-4">
                <div>
                  <p className="text-sm text-gray-600">Enseignant</p>
                  <p className="font-medium">{assignment.subjects?.profiles?.full_name || 'Non assigné'}</p>
                </div>

                <div>
                  <p className="text-sm text-gray-600">Créé le</p>
                  <p className="font-medium">
                    {format(new Date(assignment.created_at), 'PPP', { locale: fr })}
                  </p>
                </div>

                <div>
                  <p className="text-sm text-gray-600">Statut</p>
                  <p className="font-medium">
                    {new Date(assignment.due_date) < new Date() ? 'Expiré' : 'Actif'}
                  </p>
                </div>
              </CardContent>
            </Card>
          </div>
        </div>
      </div>
    </Layout>
  );
}