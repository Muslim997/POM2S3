'use client';

import { useEffect, useState, useCallback } from 'react';
import { useRouter } from 'next/navigation';
import Layout from '@/components/Layout';
import { Card, CardHeader, CardContent, CardTitle } from '@/components/Card';
import { useAuthStore } from '@/lib/store';
import { BookOpen, Users, Clock, Plus } from 'lucide-react';
import Link from 'next/link';
import Button from '@/components/Button';

export default function CoursesPage() {
  const router = useRouter();
  const { user } = useAuthStore();
  const [courses, setCourses] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);

  const loadCourses = useCallback(async () => {
    if (!user) return;

    try {
      const token = localStorage.getItem('token');
      if (!token) {
        router.push('/login');
        return;
      }

      const response = await fetch('/api/courses', {
        headers: {
          'Authorization': `Bearer ${token}`,
        },
      });

      if (response.ok) {
        const coursesData = await response.json();
        setCourses(coursesData);
      } else {
        console.error('Erreur lors de la récupération des cours');
      }
    } catch (error) {
      console.error('Error loading courses:', error);
    } finally {
      setLoading(false);
    }
  }, [user, router]);

  useEffect(() => {
    if (!user) {
      router.push('/login');
      return;
    }

    loadCourses();
  }, [user, router, loadCourses]);

  if (!user) return null;

  return (
    <Layout>
      <div className="mb-8">
        <div className="flex items-center justify-between">
          <div>
            <h1 className="text-3xl font-bold text-gray-900 mb-2">
              {user.role === 'student' ? 'Mes cours' : 'Cours'}
            </h1>
            <p className="text-gray-600">
              {user.role === 'student' && 'Liste de tous vos cours inscrits'}
              {user.role === 'teacher' && 'Gérez vos cours et contenus pédagogiques'}
              {user.role === 'admin' && 'Gestion de tous les cours de la plateforme'}
            </p>
          </div>
          {(user.role === 'teacher' || user.role === 'admin') && (
            <Link href="/courses/new">
              <Button>
                <Plus size={18} className="mr-2" />
                Nouveau cours
              </Button>
            </Link>
          )}
        </div>
      </div>

      {loading ? (
        <div className="flex justify-center items-center h-64">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
        </div>
      ) : courses.length === 0 ? (
        <Card>
          <CardContent className="text-center py-12">
            <BookOpen size={48} className="mx-auto text-gray-400 mb-4" />
            <p className="text-gray-600 mb-4">Aucun cours disponible</p>
            {(user.role === 'teacher' || user.role === 'admin') && (
              <Link href="/courses/new">
                <Button>Créer votre premier cours</Button>
              </Link>
            )}
          </CardContent>
        </Card>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {courses.map((course) => (
            <Card key={course.id} className="hover:shadow-md transition-shadow">
              <CardHeader className="bg-gradient-to-r from-blue-500 to-blue-600">
                <CardTitle className="text-white">{course.title}</CardTitle>
                <p className="text-blue-100 text-sm">{course.code}</p>
              </CardHeader>
              <CardContent>
                <p className="text-gray-600 mb-4 line-clamp-2">
                  {course.description || 'Aucune description disponible'}
                </p>

                <div className="space-y-2 mb-4">
                  <div className="flex items-center text-sm text-gray-600">
                    <Users size={16} className="mr-2" />
                    <span>Prof. {course.profiles?.full_name || 'Non assigné'}</span>
                  </div>
                  <div className="flex items-center text-sm text-gray-600">
                    <Clock size={16} className="mr-2" />
                    <span>{course.credits} crédits</span>
                  </div>
                </div>

                <Link href={`/courses/${course.id}`}>
                  <Button className="w-full" variant="secondary">
                    Voir le cours
                  </Button>
                </Link>
              </CardContent>
            </Card>
          ))}
        </div>
      )}
    </Layout>
  );
}
