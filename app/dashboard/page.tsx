'use client';

import { useEffect, useState, useCallback } from 'react';
import { useRouter } from 'next/navigation';
import Layout from '@/components/Layout';
import { Card, CardHeader, CardContent, CardTitle } from '@/components/Card';
import { useAuthStore } from '@/lib/store';
import { BookOpen, FileText, MessageSquare, Calendar, TrendingUp, Users, Award } from 'lucide-react';
import Link from 'next/link';

export default function DashboardPage() {
  const router = useRouter();
  const { user } = useAuthStore();
  const [stats, setStats] = useState<any>({});
  const [recentActivities, setRecentActivities] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);

  const loadDashboardData = useCallback(async () => {
    if (!user) return;

    try {
      const token = localStorage.getItem('token');
      if (!token) {
        router.push('/login');
        return;
      }

      const response = await fetch('/api/dashboard/stats', {
        headers: {
          'Authorization': `Bearer ${token}`,
        },
      });

      if (response.ok) {
        const statsData = await response.json();
        setStats(statsData);
      } else {
        console.error('Erreur lors de la récupération des statistiques');
      }
    } catch (error) {
      console.error('Error loading dashboard data:', error);
    } finally {
      setLoading(false);
    }
  }, [user, router]);

  useEffect(() => {
    if (!user) {
      router.push('/login');
      return;
    }

    loadDashboardData();
  }, [user, router, loadDashboardData]);

  if (!user) return null;

  const renderStudentDashboard = () => (
    <>
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
        <Card>
          <CardContent className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-600">Mes cours</p>
              <p className="text-3xl font-bold text-gray-900">{stats.courses}</p>
            </div>
            <BookOpen className="text-blue-600" size={40} />
          </CardContent>
        </Card>

        <Card>
          <CardContent className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-600">Devoirs en attente</p>
              <p className="text-3xl font-bold text-gray-900">{stats.assignments}</p>
            </div>
            <FileText className="text-orange-600" size={40} />
          </CardContent>
        </Card>

        <Card>
          <CardContent className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-600">Notifications</p>
              <p className="text-3xl font-bold text-gray-900">{stats.notifications}</p>
            </div>
            <MessageSquare className="text-green-600" size={40} />
          </CardContent>
        </Card>

        <Card>
          <CardContent className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-600">Moyenne générale</p>
              <p className="text-3xl font-bold text-gray-900">{stats.grade}</p>
            </div>
            <Award className="text-yellow-600" size={40} />
          </CardContent>
        </Card>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <Card>
          <CardHeader>
            <CardTitle>Prochains devoirs</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="space-y-4">
              <div className="flex items-center justify-between p-3 bg-gray-50 rounded-lg">
                <div>
                  <p className="font-medium text-gray-900">Projet Web Avancé</p>
                  <p className="text-sm text-gray-600">À rendre le 15/12/2025</p>
                </div>
                <Link href="/assignments">
                  <button className="text-blue-600 hover:text-blue-700">Voir</button>
                </Link>
              </div>
              <div className="flex items-center justify-between p-3 bg-gray-50 rounded-lg">
                <div>
                  <p className="font-medium text-gray-900">TP Base de données</p>
                  <p className="text-sm text-gray-600">À rendre le 18/12/2025</p>
                </div>
                <Link href="/assignments">
                  <button className="text-blue-600 hover:text-blue-700">Voir</button>
                </Link>
              </div>
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle>Activité récente</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="space-y-4">
              <div className="flex items-start space-x-3">
                <div className="flex-shrink-0 w-2 h-2 mt-2 bg-blue-600 rounded-full"></div>
                <div>
                  <p className="text-sm text-gray-900">Nouveau cours ajouté : Algorithmes avancés</p>
                  <p className="text-xs text-gray-500">Il y a 2 heures</p>
                </div>
              </div>
              <div className="flex items-start space-x-3">
                <div className="flex-shrink-0 w-2 h-2 mt-2 bg-green-600 rounded-full"></div>
                <div>
                  <p className="text-sm text-gray-900">Note publiée : Projet Mobile (18/20)</p>
                  <p className="text-xs text-gray-500">Il y a 5 heures</p>
                </div>
              </div>
              <div className="flex items-start space-x-3">
                <div className="flex-shrink-0 w-2 h-2 mt-2 bg-orange-600 rounded-full"></div>
                <div>
                  <p className="text-sm text-gray-900">Nouveau message de Prof. Martin</p>
                  <p className="text-xs text-gray-500">Hier</p>
                </div>
              </div>
            </div>
          </CardContent>
        </Card>
      </div>
    </>
  );

  const renderTeacherDashboard = () => (
    <>
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
        <Card>
          <CardContent className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-600">Mes cours</p>
              <p className="text-3xl font-bold text-gray-900">{stats.courses}</p>
            </div>
            <BookOpen className="text-blue-600" size={40} />
          </CardContent>
        </Card>

        <Card>
          <CardContent className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-600">Devoirs créés</p>
              <p className="text-3xl font-bold text-gray-900">{stats.assignments}</p>
            </div>
            <FileText className="text-orange-600" size={40} />
          </CardContent>
        </Card>

        <Card>
          <CardContent className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-600">À corriger</p>
              <p className="text-3xl font-bold text-gray-900">{stats.pendingGrading}</p>
            </div>
            <TrendingUp className="text-green-600" size={40} />
          </CardContent>
        </Card>

        <Card>
          <CardContent className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-600">Étudiants</p>
              <p className="text-3xl font-bold text-gray-900">{stats.students}</p>
            </div>
            <Users className="text-purple-600" size={40} />
          </CardContent>
        </Card>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <Card>
          <CardHeader>
            <CardTitle>Actions rapides</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="space-y-3">
              <Link href="/courses/new">
                <button className="w-full p-3 bg-blue-50 hover:bg-blue-100 text-blue-700 rounded-lg text-left font-medium transition-colors">
                  + Ajouter un nouveau cours
                </button>
              </Link>
              <Link href="/assignments/new">
                <button className="w-full p-3 bg-green-50 hover:bg-green-100 text-green-700 rounded-lg text-left font-medium transition-colors">
                  + Créer un devoir
                </button>
              </Link>
              <Link href="/announcements/new">
                <button className="w-full p-3 bg-orange-50 hover:bg-orange-100 text-orange-700 rounded-lg text-left font-medium transition-colors">
                  + Publier une annonce
                </button>
              </Link>
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle>Devoirs récents</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="space-y-4">
              <div className="flex items-center justify-between p-3 bg-gray-50 rounded-lg">
                <div>
                  <p className="font-medium text-gray-900">Projet Web Avancé</p>
                  <p className="text-sm text-gray-600">12 soumissions</p>
                </div>
                <Link href="/assignments">
                  <button className="text-blue-600 hover:text-blue-700">Corriger</button>
                </Link>
              </div>
            </div>
          </CardContent>
        </Card>
      </div>
    </>
  );

  const renderAdminDashboard = () => (
    <>
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
        <Card>
          <CardContent className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-600">Utilisateurs totaux</p>
              <p className="text-3xl font-bold text-gray-900">{stats.users}</p>
            </div>
            <Users className="text-blue-600" size={40} />
          </CardContent>
        </Card>

        <Card>
          <CardContent className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-600">Cours totaux</p>
              <p className="text-3xl font-bold text-gray-900">{stats.courses}</p>
            </div>
            <BookOpen className="text-green-600" size={40} />
          </CardContent>
        </Card>

        <Card>
          <CardContent className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-600">Étudiants</p>
              <p className="text-3xl font-bold text-gray-900">{stats.students}</p>
            </div>
            <Users className="text-orange-600" size={40} />
          </CardContent>
        </Card>

        <Card>
          <CardContent className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-600">Enseignants</p>
              <p className="text-3xl font-bold text-gray-900">{stats.teachers}</p>
            </div>
            <Users className="text-purple-600" size={40} />
          </CardContent>
        </Card>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <Card>
          <CardHeader>
            <CardTitle>Gestion rapide</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="space-y-3">
              <Link href="/users">
                <button className="w-full p-3 bg-blue-50 hover:bg-blue-100 text-blue-700 rounded-lg text-left font-medium transition-colors">
                  Gérer les utilisateurs
                </button>
              </Link>
              <Link href="/courses">
                <button className="w-full p-3 bg-green-50 hover:bg-green-100 text-green-700 rounded-lg text-left font-medium transition-colors">
                  Gérer les cours
                </button>
              </Link>
              <Link href="/analytics">
                <button className="w-full p-3 bg-purple-50 hover:bg-purple-100 text-purple-700 rounded-lg text-left font-medium transition-colors">
                  Voir les statistiques
                </button>
              </Link>
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle>Activité système</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="space-y-4">
              <div className="flex items-start space-x-3">
                <div className="flex-shrink-0 w-2 h-2 mt-2 bg-blue-600 rounded-full"></div>
                <div>
                  <p className="text-sm text-gray-900">Nouvelle inscription : Marie Dubois (Étudiante)</p>
                  <p className="text-xs text-gray-500">Il y a 1 heure</p>
                </div>
              </div>
              <div className="flex items-start space-x-3">
                <div className="flex-shrink-0 w-2 h-2 mt-2 bg-green-600 rounded-full"></div>
                <div>
                  <p className="text-sm text-gray-900">Nouveau cours créé : Machine Learning</p>
                  <p className="text-xs text-gray-500">Il y a 3 heures</p>
                </div>
              </div>
            </div>
          </CardContent>
        </Card>
      </div>
    </>
  );

  return (
    <Layout>
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-gray-900 mb-2">
          Bonjour, {user.full_name} !
        </h1>
        <p className="text-gray-600">
          {user.role === 'student' && 'Voici un aperçu de votre parcours académique'}
          {user.role === 'teacher' && 'Voici un aperçu de votre activité d\'enseignement'}
          {user.role === 'admin' && 'Voici un aperçu de la plateforme'}
        </p>
      </div>

      {loading ? (
        <div className="flex justify-center items-center h-64">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
        </div>
      ) : (
        <>
          {user.role === 'student' && renderStudentDashboard()}
          {user.role === 'teacher' && renderTeacherDashboard()}
          {user.role === 'admin' && renderAdminDashboard()}
        </>
      )}
    </Layout>
  );
}
